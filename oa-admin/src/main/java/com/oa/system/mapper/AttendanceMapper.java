package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤Mapper接口
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 查询考勤记录列表
     */
    List<Attendance> selectAttendanceList(Attendance attendance);

    /**
     * 查询用户某天的考勤记录
     */
    Attendance selectAttendanceByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 查询考勤统计
     */
    List<Attendance> selectAttendanceStatistics(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
