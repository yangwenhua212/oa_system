package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService {

    /**
     * 打卡签到
     */
    Attendance clockIn();

    /**
     * 打卡签退
     */
    Attendance clockOut();

    /**
     * 获取今日考勤状态
     */
    Attendance getTodayAttendance();

    /**
     * 分页查询考勤记录
     */
    PageR<Attendance> getAttendancePage(PageDTO<Attendance> pageDTO,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        String realName,
                                        String clockInStatus);

    /**
     * 获取考勤统计
     */
    Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取考勤日历数据
     */
    List<Attendance> getAttendanceCalendar(LocalDate startDate, LocalDate endDate);
}
