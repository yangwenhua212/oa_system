package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程Mapper接口
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 查询日程列表
     */
    List<Schedule> selectScheduleList(Schedule schedule);

    /**
     * 查询我的日程
     */
    List<Schedule> selectMyScheduleList(@Param("userId") Long userId);

    /**
     * 查询日历范围内的日程
     */
    List<Schedule> selectScheduleByDateRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日日程
     */
    List<Schedule> selectTodaySchedule(@Param("userId") Long userId);
}
