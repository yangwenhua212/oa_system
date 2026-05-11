package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;
    private final UserMapper userMapper;

    @Override
    public List<Dept> getDeptTree() {
        List<Dept> allDepts = deptMapper.selectDeptTree();
        return buildDeptTree(allDepts);
    }

    /**
     * 构建部门树
     */
    private List<Dept> buildDeptTree(List<Dept> depts) {
        List<Dept> result = new ArrayList<>();
        Map<Long, List<Dept>> childrenMap = depts.stream()
                .filter(d -> d.getParentId() != 0)
                .collect(Collectors.groupingBy(Dept::getParentId));

        for (Dept dept : depts) {
            if (dept.getParentId() == 0) {
                result.add(dept);
            }
            List<Dept> children = childrenMap.get(dept.getId());
            if (children != null) {
                dept.setChildren(children);
            } else {
                dept.setChildren(new ArrayList<>());
            }
        }

        return result;
    }

    @Override
    public PageR<Dept> getDeptPage(PageDTO<Dept> pageDTO) {
        Page<Dept> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Dept params = pageDTO.getParams() != null ? pageDTO.getParams() : new Dept();

        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getDeptName() != null, Dept::getDeptName, params.getDeptName())
                .eq(params.getStatus() != null, Dept::getStatus, params.getStatus())
                .orderByAsc(Dept::getSort);

        Page<Dept> result = deptMapper.selectPage(page, wrapper);

        // 填充负责人名称
        for (Dept dept : result.getRecords()) {
            if (dept.getLeader() != null) {
                User leader = userMapper.selectById(dept.getLeader());
                if (leader != null) {
                    dept.setLeaderName(leader.getRealName());
                }
            }
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public Dept getDeptById(Long id) {
        Dept dept = deptMapper.selectById(id);
        if (dept != null && dept.getLeader() != null) {
            User leader = userMapper.selectById(dept.getLeader());
            if (leader != null) {
                dept.setLeaderName(leader.getRealName());
            }
        }
        return dept;
    }

    @Override
    @Transactional
    public Long addDept(Dept dept) {
        deptMapper.insert(dept);
        return dept.getId();
    }

    @Override
    public boolean updateDept(Dept dept) {
        deptMapper.updateById(dept);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteDept(Long id) {
        // 检查是否有子部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, id);
        Long count = deptMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("存在下级部门，无法删除");
        }

        // 检查是否有用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeptId, id);
        Long userCount = userMapper.selectCount(userWrapper);
        if (userCount > 0) {
            throw new RuntimeException("部门下存在用户，无法删除");
        }

        deptMapper.deleteById(id);
        return true;
    }

    @Override
    public List<Dept> getAllDepts() {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getStatus, 1)
                .orderByAsc(Dept::getSort);
        return deptMapper.selectList(wrapper);
    }
}
