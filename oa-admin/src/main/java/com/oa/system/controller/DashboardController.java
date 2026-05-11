package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.mapper.NoticeMapper;
import com.oa.system.mapper.SysLogMapper;
import com.oa.system.mapper.TaskMapper;
import com.oa.system.mapper.AttendanceMapper;
import com.oa.system.service.AttendanceService;
import com.oa.system.service.NoticeService;
import com.oa.system.service.ScheduleService;
import com.oa.system.service.TaskService;
import com.oa.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 工作台/首页控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final AttendanceService attendanceService;
    private final ScheduleService scheduleService;
    private final TaskService taskService;
    private final NoticeService noticeService;
    private final SysLogMapper sysLogMapper;

    /**
     * 获取工作台数据
     */
    @GetMapping("/data")
    public R getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 获取当前用户信息
        data.put("user", userService.getCurrentUser());

        // 获取今日考勤
        data.put("todayAttendance", attendanceService.getTodayAttendance());

        // 获取今日日程
        data.put("todaySchedule", scheduleService.getTodaySchedule());

        // 获取我的任务
        data.put("myTasks", taskService.getMyTaskList());

        // 获取我的通知
        data.put("myNotices", noticeService.getMyNoticeList());

        // 获取未读数量
        Map<String, Long> unreadCount = new HashMap<>();
        unreadCount.put("notice", noticeService.getUnreadCount());
        unreadCount.put("mail", 0L); // TODO: 获取未读邮件数
        data.put("unreadCount", unreadCount);

        return R.ok(data);
    }

    /**
     * 获取某周的系统使用量统计（每天登录次数和操作次数）
     * @param weekStart 周一的日期，格式 yyyy-MM-dd，不传则使用当前周
     */
    @GetMapping("/weekly-usage")
    public R getWeeklyUsage(@RequestParam(required = false) String weekStart) {
        LocalDate monday;
        if (weekStart != null) {
            monday = LocalDate.parse(weekStart);
        } else {
            monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        LocalDate sunday = monday.plusDays(6);

        // 查询数据库中的每日统计
        List<Map<String, Object>> dbStats = sysLogMapper.selectDailyOperationStats(monday, sunday);

        // 组装为7天的完整数组（周一~周日），没有数据的日期补0
        Map<String, Map<String, Object>> statsMap = new HashMap<>();
        for (Map<String, Object> row : dbStats) {
            statsMap.put(row.get("date").toString(), row);
        }

        int[] loginData = new int[7];
        int[] actionData = new int[7];
        String[] dayLabels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            Map<String, Object> dayStat = statsMap.get(date.toString());
            loginData[i] = dayStat != null ? ((Number) dayStat.get("loginCount")).intValue() : 0;
            actionData[i] = dayStat != null ? ((Number) dayStat.get("actionCount")).intValue() : 0;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("days", dayLabels);
        result.put("loginCount", loginData);
        result.put("actionCount", actionData);
        return R.ok(result);
    }
}
