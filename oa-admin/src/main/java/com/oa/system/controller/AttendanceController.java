package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Attendance;
import com.oa.system.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 打卡签到
     */
    @PostMapping("/clockIn")
    public R clockIn() {
        Attendance attendance = attendanceService.clockIn();
        return R.ok(attendance);
    }

    /**
     * 打卡签退
     */
    @PostMapping("/clockOut")
    public R clockOut() {
        Attendance attendance = attendanceService.clockOut();
        return R.ok(attendance);
    }

    /**
     * 获取今日考勤状态
     */
    @GetMapping("/today")
    public R getTodayAttendance() {
        Attendance attendance = attendanceService.getTodayAttendance();
        return R.ok(attendance);
    }

    /**
     * 分页查询考勤记录
     */
    @GetMapping("/list")
    public R list(PageDTO<Attendance> pageDTO,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                  @RequestParam(required = false) String realName,
                  @RequestParam(required = false) String clockInStatus) {
        PageR<Attendance> result = attendanceService.getAttendancePage(pageDTO, startDate, endDate, realName, clockInStatus);
        return R.ok(result);
    }

    /**
     * 获取考勤统计
     */
    @GetMapping("/statistics")
    public R statistics(@RequestParam(required = false) Long userId,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> statistics = attendanceService.getAttendanceStatistics(userId, startDate, endDate);
        return R.ok(statistics);
    }

    /**
     * 获取考勤日历数据
     */
    @GetMapping("/calendar")
    public R calendar(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Attendance> list = attendanceService.getAttendanceCalendar(startDate, endDate);
        return R.ok(list);
    }
}
