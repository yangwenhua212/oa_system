package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Schedule;
import com.oa.system.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程管理控制器
 */
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 创建日程
     */
    @PostMapping
    public R create(@RequestBody Schedule schedule) {
        Long id = scheduleService.createSchedule(schedule);
        return R.ok(id);
    }

    /**
     * 更新日程
     */
    @PutMapping
    public R update(@RequestBody Schedule schedule) {
        scheduleService.updateSchedule(schedule);
        return R.ok();
    }

    /**
     * 删除日程
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return R.ok();
    }

    /**
     * 获取日程详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return R.ok(schedule);
    }

    /**
     * 分页查询日程列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Schedule> pageDTO) {
        PageR<Schedule> result = scheduleService.getSchedulePage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的日程列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Schedule> list = scheduleService.getMyScheduleList();
        return R.ok(list);
    }

    /**
     * 获取日历范围内的日程
     */
    @GetMapping("/range")
    public R range(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<Schedule> list = scheduleService.getScheduleByDateRange(startTime, endTime);
        return R.ok(list);
    }

    /**
     * 获取今日日程
     */
    @GetMapping("/today")
    public R today() {
        List<Schedule> list = scheduleService.getTodaySchedule();
        return R.ok(list);
    }
}
