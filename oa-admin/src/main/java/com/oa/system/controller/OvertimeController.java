package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Overtime;
import com.oa.system.service.OvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 加班管理控制器
 */
@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    /**
     * 提交加班申请
     */
    @PostMapping
    public R submit(@RequestBody Overtime overtime) {
        Long id = overtimeService.submitOvertime(overtime);
        return R.ok(id);
    }

    /**
     * 更新加班申请
     */
    @PutMapping
    public R update(@RequestBody Overtime overtime) {
        overtimeService.updateOvertime(overtime);
        return R.ok();
    }

    /**
     * 取消加班申请
     */
    @PutMapping("/cancel/{id}")
    public R cancel(@PathVariable Long id) {
        overtimeService.cancelOvertime(id);
        return R.ok();
    }

    /**
     * 获取加班详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Overtime overtime = overtimeService.getOvertimeById(id);
        return R.ok(overtime);
    }

    /**
     * 分页查询加班列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Overtime> pageDTO) {
        PageR<Overtime> result = overtimeService.getOvertimePage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的加班列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Overtime> list = overtimeService.getMyOvertimeList();
        return R.ok(list);
    }
}
