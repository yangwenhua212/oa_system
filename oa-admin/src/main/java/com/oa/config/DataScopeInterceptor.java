package com.oa.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.oa.system.entity.Role;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.RoleMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据范围拦截器
 * 根据当前用户角色的 dataScope 自动追加 SQL 过滤条件
 */
@Slf4j
@Component
public class DataScopeInterceptor implements InnerInterceptor {

    /**
     * 防止递归调用的 ThreadLocal 标志
     */
    private static final ThreadLocal<Boolean> INTERCEPTING = ThreadLocal.withInitial(() -> false);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Lazy
    @Autowired
    private UserMapper userMapper;
    @Lazy
    @Autowired
    private RoleMapper roleMapper;
    @Lazy
    @Autowired
    private DeptMapper deptMapper;

    /**
     * 需要进行数据范围过滤的 Mapper 命名空间（白名单）
     * key=Mapper全限定名, value=该表用于部门过滤的列名
     */
    private static final Map<String, String> SCOPE_MAPPER_DEPT = new HashMap<>();

    /**
     * 需要进行"仅本人"过滤的 Mapper 命名空间（仅有 create_by 的表）
     */
    private static final Set<String> SCOPE_MAPPER_SELF = new HashSet<>();

    static {
        // ---- 有 dept_id 的表，可按部门过滤 ----
        SCOPE_MAPPER_DEPT.put("com.oa.system.mapper.UserMapper", "dept_id");
        SCOPE_MAPPER_DEPT.put("com.oa.system.mapper.AttendanceMapper", "dept_id");
        SCOPE_MAPPER_DEPT.put("com.oa.system.mapper.LeaveMapper", "dept_id");
        SCOPE_MAPPER_DEPT.put("com.oa.system.mapper.OvertimeMapper", "dept_id");
        SCOPE_MAPPER_DEPT.put("com.oa.system.mapper.TaskMapper", "dept_id");

        // ---- 仅有 create_by 的表，仅支持"本人数据"过滤 ----
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.NoticeMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.ScheduleMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.MailMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.NoteMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.NoteCatalogMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.AddressBookMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.AddressGroupMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.FileMapper");
        SCOPE_MAPPER_SELF.add("com.oa.system.mapper.FilePathMapper");
    }

    /**
     * 从 sql 中提取表名（支持反引号）
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 防止递归调用（拦截器内部查询数据库时不再进行数据范围过滤）
        if (INTERCEPTING.get()) {
            return;
        }

        // 仅拦截 SELECT
        if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }

        // 获取当前用户信息
        INTERCEPTING.set(true);
        try {
            DataScopeInfo scopeInfo = getCurrentUserDataScope();
            if (scopeInfo == null) {
                return;
            }

            // 根据 Mapper 命名空间判断是否需要过滤
            String mapperId = ms.getId();
            String namespace = mapperId.substring(0, mapperId.lastIndexOf('.'));

            boolean hasDeptFilter = SCOPE_MAPPER_DEPT.containsKey(namespace);
            boolean hasSelfFilter = SCOPE_MAPPER_SELF.contains(namespace);

            if (!hasDeptFilter && !hasSelfFilter) {
                return;
            }

            // 构建过滤条件
            StringBuilder filterSql = new StringBuilder();
            String sql = boundSql.getSql();

            // 获取表别名（如果有）
            String tableAlias = extractTableAlias(sql);
            String prefix = tableAlias != null ? tableAlias + "." : "";

            boolean needComma = sql.toUpperCase().contains("WHERE");

            switch (scopeInfo.dataScope) {
                case 2: // 本部门数据
                    if (hasDeptFilter && scopeInfo.deptId != null) {
                        filterSql.append(needComma ? " AND " : " WHERE ");
                        filterSql.append(prefix).append(SCOPE_MAPPER_DEPT.get(namespace))
                                .append(" = ").append(scopeInfo.deptId);
                    }
                    break;

                case 3: // 本部门及以下
                    if (hasDeptFilter && scopeInfo.deptId != null) {
                        List<Long> allDeptIds = getAllChildDeptIds(scopeInfo.deptId);
                        allDeptIds.add(scopeInfo.deptId);
                        String ids = allDeptIds.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        filterSql.append(needComma ? " AND " : " WHERE ");
                        filterSql.append(prefix).append(SCOPE_MAPPER_DEPT.get(namespace))
                                .append(" IN (").append(ids).append(")");
                    }
                    break;

                case 4: // 仅本人数据
                    if (hasSelfFilter) {
                        filterSql.append(needComma ? " AND " : " WHERE ");
                        filterSql.append(prefix).append("create_by = ").append(scopeInfo.userId);
                    } else if (hasDeptFilter) {
                        filterSql.append(needComma ? " AND " : " WHERE ");
                        filterSql.append(prefix).append("create_by = ").append(scopeInfo.userId);
                    }
                    break;

                default: // 1-全部数据，不过滤
                    break;
            }

            if (filterSql.length() > 0) {
                String newSql = insertBeforeOrderBy(sql, filterSql.toString());
                log.debug("DataScope filter applied: {}", newSql);
                PluginUtils.mpBoundSql(boundSql).sql(newSql);
            }
        } finally {
            INTERCEPTING.remove();
        }
    }

    /**
     * 提取表别名
     */
    private String extractTableAlias(String sql) {
        Pattern pattern = Pattern.compile(
                "\\bFROM\\s+`?[a-zA-Z_][a-zA-Z0-9_]*`?\\s+([a-zA-Z_][a-zA-Z0-9_]*)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(sql);
        String alias = null;
        while (matcher.find()) {
            alias = matcher.group(1);
        }
        return alias;
    }

    /**
     * 在 ORDER BY / GROUP BY / LIMIT 之前插入过滤条件
     */
    private String insertBeforeOrderBy(String sql, String filterClause) {
        // 查找 ORDER BY / GROUP BY / LIMIT 并插入在前面
        String upper = sql.toUpperCase();
        int orderByIdx = upper.lastIndexOf("ORDER BY");
        int groupByIdx = upper.lastIndexOf("GROUP BY");
        int limitIdx = upper.lastIndexOf("LIMIT");

        int insertPos = -1;
        if (orderByIdx >= 0) insertPos = orderByIdx;
        if (groupByIdx >= 0 && (insertPos < 0 || groupByIdx < insertPos)) insertPos = groupByIdx;
        if (limitIdx >= 0 && (insertPos < 0 || limitIdx < insertPos)) insertPos = limitIdx;

        if (insertPos >= 0) {
            return sql.substring(0, insertPos) + filterClause + " " + sql.substring(insertPos);
        } else {
            return sql + filterClause;
        }
    }

    /**
     * 递归获取所有子部门 ID（包括自身）
     */
    private List<Long> getAllChildDeptIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        List<Long> children = deptMapper.selectChildrenDeptIds(parentId);
        if (children != null) {
            for (Long childId : children) {
                result.add(childId);
                result.addAll(getAllChildDeptIds(childId));
            }
        }
        return result;
    }

    /**
     * 获取当前用户的数据范围信息
     * 返回 null 表示不进行过滤（未登录或管理员）
     */
    private DataScopeInfo getCurrentUserDataScope() {
        try {
            String token = jwtTokenUtil.getTokenFromRequest();
            if (token == null) return null;

            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            if (userId == null) return null;

            User user = userMapper.selectById(userId);
            if (user == null) return null;

            // 管理员不进行数据范围过滤
            if (user.getIsAdmin() != null && user.getIsAdmin() == 1) {
                return null;
            }

            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            if (roles == null || roles.isEmpty()) return null;

            // 取最严格的数据范围（数字越大越严格）
            Integer dataScope = roles.stream()
                    .map(Role::getDataScope)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(1);

            return new DataScopeInfo(userId, user.getDeptId(), dataScope);
        } catch (Exception e) {
            log.warn("获取数据范围信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 数据范围信息
     */
    private static class DataScopeInfo {
        final Long userId;
        final Long deptId;
        final int dataScope; // 1-全部, 2-本部门, 3-本部门及以下, 4-仅本人

        DataScopeInfo(Long userId, Long deptId, int dataScope) {
            this.userId = userId;
            this.deptId = deptId;
            this.dataScope = dataScope;
        }
    }
}
