package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Schedule;
import com.oa.system.mapper.ScheduleMapper;
import com.oa.system.service.ScheduleService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程服务实现类
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Long createSchedule(Schedule schedule) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        schedule.setUserId(userId);
        schedule.setStatus(1);
        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    public boolean updateSchedule(Schedule schedule) {
        scheduleMapper.updateById(schedule);
        return true;
    }

    @Override
    public boolean deleteSchedule(Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new RuntimeException("日程不存在");
        }
        if (!userId.equals(schedule.getUserId())) {
            throw new RuntimeException("只能删除自己的日程");
        }
        scheduleMapper.deleteById(id);
        return true;
    }

    @Override
    public Schedule getScheduleById(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public PageR<Schedule> getSchedulePage(PageDTO<Schedule> pageDTO) {
        Page<Schedule> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Schedule params = pageDTO.getParams() != null ? pageDTO.getParams() : new Schedule();

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getTitle() != null, Schedule::getTitle, params.getTitle())
                .orderByDesc(Schedule::getStartTime);

        Page<Schedule> result = scheduleMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Schedule> getMyScheduleList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return scheduleMapper.selectMyScheduleList(userId);
    }

    @Override
    public List<Schedule> getScheduleByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return scheduleMapper.selectScheduleByDateRange(userId, startTime, endTime);
    }

    @Override
    public List<Schedule> getTodaySchedule() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return scheduleMapper.selectTodaySchedule(userId);
    }
}
