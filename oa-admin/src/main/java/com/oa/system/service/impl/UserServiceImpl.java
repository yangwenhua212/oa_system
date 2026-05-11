package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;
import com.oa.system.entity.Menu;
import com.oa.system.entity.Position;
import com.oa.system.entity.Role;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.MenuMapper;
import com.oa.system.mapper.PositionMapper;
import com.oa.system.mapper.RoleMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.UserService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MenuMapper menuMapper;
    private final DeptMapper deptMapper;
    private final PositionMapper positionMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新登录信息
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId())
                .set(User::getLastLoginIp, "127.0.0.1")
                .set(User::getLastLoginTime, LocalDateTime.now())
                .setSql("login_count = login_count + 1");
        userMapper.update(null, updateWrapper);

        // 填充部门信息
        if (user.getDeptId() != null) {
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                user.setDeptName(dept.getDeptName());
            }
        }

        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            user.setPassword(null);
            if (user.getDeptId() != null) {
                Dept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    user.setDeptName(dept.getDeptName());
                }
            }
        }
        return user;
    }

    @Override
    public User getCurrentUser() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            if (user.getDeptId() != null) {
                Dept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    user.setDeptName(dept.getDeptName());
                }
            }
        }
        return user;
    }

    @Override
    public List getCurrentUserMenus() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        if (user == null) {
            return new ArrayList<>();
        }

        List<Menu> menus;
        if (user.getIsAdmin() == 1) {
            // 管理员获取所有菜单
            menus = menuMapper.selectMenuTreeAll();
        } else {
            // 普通用户获取对应菜单
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }

        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     */
    private List buildMenuTree(List<Menu> menus) {
        List<Menu> result = new ArrayList<>();
        Map<Long, List<Menu>> childrenMap = new HashMap<>();

        for (Menu menu : menus) {
            if (menu.getParentId() == 0) {
                result.add(menu);
            } else {
                childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
            }
        }

        for (Menu menu : result) {
            menu.setChildren(childrenMap.getOrDefault(menu.getId(), new ArrayList<>()));
        }

        return result;
    }

    @Override
    public PageR<User> getUserPage(PageDTO<User> pageDTO) {
        Page<User> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        User params = pageDTO.getParams() != null ? pageDTO.getParams() : new User();

        Page<User> result;

        // 有关键词时使用多表联合搜索
        if (params.getPhone() != null && !params.getPhone().isEmpty()) {
            List<User> list = userMapper.searchUserByKeyword(params.getPhone());
            int total = list.size();
            int from = (int) ((page.getCurrent() - 1) * page.getSize());
            int to = (int) Math.min(from + page.getSize(), total);
            result = new Page<>(page.getCurrent(), page.getSize(), total);
            result.setRecords(from < total ? list.subList(from, to) : List.of());
        } else {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(params.getUsername() != null, User::getUsername, params.getUsername())
                    .like(params.getRealName() != null, User::getRealName, params.getRealName())
                    .eq(params.getStatus() != null, User::getStatus, params.getStatus())
                    .eq(params.getDeptId() != null, User::getDeptId, params.getDeptId())
                    .orderByDesc(User::getCreateTime);
            result = userMapper.selectPage(page, wrapper);
        }

        // 填充部门名称、职位名称和角色名称
        for (User user : result.getRecords()) {
            user.setPassword(null);
            if (user.getDeptId() != null) {
                Dept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) user.setDeptName(dept.getDeptName());
            }
            if (user.getPositionId() != null) {
                Position position = positionMapper.selectById(user.getPositionId());
                if (position != null) user.setPositionName(position.getPositionName());
            }
            // 填充角色名称
            List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
            if (roles != null && !roles.isEmpty()) {
                user.setRoleNames(roles.stream().map(Role::getRoleName).collect(Collectors.joining(", ")));
            }
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
            if (user.getDeptId() != null) {
                Dept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    user.setDeptName(dept.getDeptName());
                }
            }
            // 填充角色名称
            List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
            if (roles != null && !roles.isEmpty()) {
                user.setRoleIds(roles.stream().map(Role::getId).toArray(Long[]::new));
                user.setRoleNames(roles.stream().map(Role::getRoleName).collect(Collectors.joining(", ")));
            }
        }
        return user;
    }

    @Override
    @Transactional
    public Long addUser(User user) {
        // 检查用户名是否重复
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码（默认123456）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("123456"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setLoginCount(0);

        userMapper.insert(user);

        // 分配角色
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            userMapper.batchInsertUserRole(user.getId(), user.getRoleIds());
        }

        return user.getId();
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果修改了用户名，检查重复
        if (user.getUsername() != null && !user.getUsername().equals(existUser.getUsername())) {
            User sameUser = userMapper.selectByUsername(user.getUsername());
            if (sameUser != null) {
                throw new RuntimeException("用户名已存在");
            }
        }

        // 不更新密码字段（密码修改单独处理）
        user.setPassword(null);
        userMapper.updateById(user);

        // 更新角色
        if (user.getRoleIds() != null) {
            userMapper.deleteUserRole(user.getId());
            if (user.getRoleIds().length > 0) {
                userMapper.batchInsertUserRole(user.getId(), user.getRoleIds());
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (id == 1) {
            throw new RuntimeException("不能删除超级管理员");
        }
        userMapper.deleteUserRole(id);
        userMapper.deleteById(id);
        return true;
    }

    @Override
    public boolean resetPassword(Long id, String password) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id)
                .set(User::getPassword, passwordEncoder.encode(password));
        userMapper.update(null, updateWrapper);

        return true;
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(newPassword));
        userMapper.update(null, updateWrapper);

        return true;
    }

    @Override
    public List<User> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1)
                .orderByAsc(User::getDeptId)
                .orderByAsc(User::getCreateTime);

        List<User> users = userMapper.selectList(wrapper);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    @Override
    public List<User> getUsersByDept(Long deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeptId, deptId)
                .eq(User::getStatus, 1)
                .orderByAsc(User::getCreateTime);

        List<User> users = userMapper.selectList(wrapper);
        users.forEach(u -> u.setPassword(null));
        return users;
    }
}
