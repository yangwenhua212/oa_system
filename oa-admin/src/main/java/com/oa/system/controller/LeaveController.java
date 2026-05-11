package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Leave;
import com.oa.system.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 请假管理控制器
 */
@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    /**
     * 提交请假申请
     */
    @PostMapping
    public R submit(@RequestBody Leave leave) {
        Long id = leaveService.submitLeave(leave);
        return R.ok(id);
    }

    /**
     * 更新请假申请
     */
    @PutMapping
    public R update(@RequestBody Leave leave) {
        leaveService.updateLeave(leave);
        return R.ok();
    }

    /**
     * 取消请假申请
     */
    @PutMapping("/cancel/{id}")
    public R cancel(@PathVariable Long id) {
        leaveService.cancelLeave(id);
        return R.ok();
    }

    /**
     * 获取请假详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Leave leave = leaveService.getLeaveById(id);
        return R.ok(leave);
    }

    /**
     * 分页查询请假列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Leave> pageDTO) {
        PageR<Leave> result = leaveService.getLeavePage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的请假列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Leave> list = leaveService.getMyLeaveList();
        return R.ok(list);
    }
}
