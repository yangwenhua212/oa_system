package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程服务接口
 */
public interface ScheduleService {

    /**
     * 创建日程
     */
    Long createSchedule(Schedule schedule);

    /**
     * 更新日程
     */
    boolean updateSchedule(Schedule schedule);

    /**
     * 删除日程
     */
    boolean deleteSchedule(Long id);

    /**
     * 获取日程详情
     */
    Schedule getScheduleById(Long id);

    /**
     * 分页查询日程列表
     */
    PageR<Schedule> getSchedulePage(PageDTO<Schedule> pageDTO);

    /**
     * 获取我的日程列表
     */
    List<Schedule> getMyScheduleList();

    /**
     * 获取日历范围内的日程
     */
    List<Schedule> getScheduleByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取今日日程
     */
    List<Schedule> getTodaySchedule();
}
