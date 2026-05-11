package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;
import com.oa.system.entity.Overtime;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.OvertimeMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.OvertimeService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 加班服务实现类
 */
@Service
@RequiredArgsConstructor
public class OvertimeServiceImpl implements OvertimeService {

    private final OvertimeMapper overtimeMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public Long submitOvertime(Overtime overtime) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        overtime.setUserId(userId);
        overtime.setUsername(user.getUsername());
        overtime.setRealName(user.getRealName());
        overtime.setStatus("pending");

        if (user.getDeptId() != null) {
            overtime.setDeptId(user.getDeptId());
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                overtime.setDeptName(dept.getDeptName());
            }
        }

        // 计算加班时长
        if (overtime.getStartTime() != null && overtime.getEndTime() != null) {
            Duration duration = Duration.between(overtime.getStartTime(), overtime.getEndTime());
            BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            overtime.setDuration(hours);
        }

        // 判断加班类型
        if (overtime.getStartTime() != null) {
            LocalDateTime date = overtime.getStartTime();
            int dayOfWeek = date.getDayOfWeek().getValue();
            // 周末加班
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                overtime.setOvertimeType("weekend");
            } else {
                overtime.setOvertimeType("weekday");
            }
        }

        overtimeMapper.insert(overtime);
        return overtime.getId();
    }

    @Override
    public boolean updateOvertime(Overtime overtime) {
        Overtime exist = overtimeMapper.selectById(overtime.getId());
        if (exist == null) {
            throw new RuntimeException("加班记录不存在");
        }
        if (!"draft".equals(exist.getStatus())) {
            throw new RuntimeException("只能修改草稿状态的申请");
        }

        // 重新计算时长
        if (overtime.getStartTime() != null && overtime.getEndTime() != null) {
            Duration duration = Duration.between(overtime.getStartTime(), overtime.getEndTime());
            BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            overtime.setDuration(hours);
        }

        overtimeMapper.updateById(overtime);
        return true;
    }

    @Override
    @Transactional
    public boolean cancelOvertime(Long id) {
        Overtime overtime = overtimeMapper.selectById(id);
        if (overtime == null) {
            throw new RuntimeException("加班记录不存在");
        }

        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        if (!userId.equals(overtime.getUserId())) {
            throw new RuntimeException("只能取消自己的加班申请");
        }

        overtime.setStatus("cancelled");
        overtimeMapper.updateById(overtime);
        return true;
    }

    @Override
    public Overtime getOvertimeById(Long id) {
        Overtime overtime = overtimeMapper.selectById(id);
        if (overtime != null && overtime.getDeptId() != null) {
            Dept dept = deptMapper.selectById(overtime.getDeptId());
            if (dept != null) {
                overtime.setDeptName(dept.getDeptName());
            }
        }
        return overtime;
    }

    @Override
    public PageR<Overtime> getOvertimePage(PageDTO<Overtime> pageDTO) {
        Page<Overtime> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Overtime params = pageDTO.getParams() != null ? pageDTO.getParams() : new Overtime();

        LambdaQueryWrapper<Overtime> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getRealName() != null, Overtime::getRealName, params.getRealName())
                .eq(params.getOvertimeType() != null, Overtime::getOvertimeType, params.getOvertimeType())
                .eq(params.getStatus() != null, Overtime::getStatus, params.getStatus())
                .orderByDesc(Overtime::getCreateTime);

        Page<Overtime> result = overtimeMapper.selectPage(page, wrapper);

        // 填充部门名称
        for (Overtime overtime : result.getRecords()) {
            if (overtime.getDeptId() != null) {
                Dept dept = deptMapper.selectById(overtime.getDeptId());
                if (dept != null) {
                    overtime.setDeptName(dept.getDeptName());
                }
            }
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Overtime> getMyOvertimeList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        LambdaQueryWrapper<Overtime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Overtime::getUserId, userId)
                .orderByDesc(Overtime::getCreateTime);
        return overtimeMapper.selectList(wrapper);
    }
}
