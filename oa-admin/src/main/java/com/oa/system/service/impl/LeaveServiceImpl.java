package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;
import com.oa.system.entity.Leave;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.LeaveMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.LeaveService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 请假服务实现类
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveMapper leaveMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public Long submitLeave(Leave leave) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        leave.setUserId(userId);
        leave.setUsername(user.getUsername());
        leave.setRealName(user.getRealName());
        leave.setStatus("pending");

        if (user.getDeptId() != null) {
            leave.setDeptId(user.getDeptId());
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                leave.setDeptName(dept.getDeptName());
            }
        }

        leaveMapper.insert(leave);
        return leave.getId();
    }

    @Override
    public boolean updateLeave(Leave leave) {
        Leave exist = leaveMapper.selectById(leave.getId());
        if (exist == null) {
            throw new RuntimeException("请假记录不存在");
        }
        if (!"draft".equals(exist.getStatus())) {
            throw new RuntimeException("只能修改草稿状态的申请");
        }
        leaveMapper.updateById(leave);
        return true;
    }

    @Override
    @Transactional
    public boolean cancelLeave(Long id) {
        Leave leave = leaveMapper.selectById(id);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        if (!userId.equals(leave.getUserId())) {
            throw new RuntimeException("只能取消自己的请假申请");
        }

        leave.setStatus("cancelled");
        leaveMapper.updateById(leave);
        return true;
    }

    @Override
    public Leave getLeaveById(Long id) {
        Leave leave = leaveMapper.selectById(id);
        if (leave != null && leave.getDeptId() != null) {
            Dept dept = deptMapper.selectById(leave.getDeptId());
            if (dept != null) {
                leave.setDeptName(dept.getDeptName());
            }
        }
        return leave;
    }

    @Override
    public PageR<Leave> getLeavePage(PageDTO<Leave> pageDTO) {
        Page<Leave> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Leave params = pageDTO.getParams() != null ? pageDTO.getParams() : new Leave();

        LambdaQueryWrapper<Leave> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getRealName() != null, Leave::getRealName, params.getRealName())
                .eq(params.getLeaveType() != null, Leave::getLeaveType, params.getLeaveType())
                .eq(params.getStatus() != null, Leave::getStatus, params.getStatus())
                .orderByDesc(Leave::getCreateTime);

        Page<Leave> result = leaveMapper.selectPage(page, wrapper);

        // 填充部门名称
        for (Leave leave : result.getRecords()) {
            if (leave.getDeptId() != null) {
                Dept dept = deptMapper.selectById(leave.getDeptId());
                if (dept != null) {
                    leave.setDeptName(dept.getDeptName());
                }
            }
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Leave> getMyLeaveList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return leaveMapper.selectMyLeaveList(userId);
    }
}
