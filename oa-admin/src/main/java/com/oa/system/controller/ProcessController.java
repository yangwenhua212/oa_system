package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.ApprovalRecord;
import com.oa.system.entity.ProcessInstance;
import com.oa.system.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    /** 提交审批 */
    @PostMapping
    public R submit(@RequestBody Map<String, Object> formData) {
        Long id = processService.submitProcess(formData);
        return R.ok(id);
    }

    /** 审批操作 */
    @PutMapping("/approve/{id}")
    public R approve(@PathVariable Long id, @RequestBody Map<String, String> params) {
        processService.approve(id, params.get("action"), params.get("comment"));
        return R.ok();
    }

    /** 获取我的申请列表 */
    @GetMapping("/my")
    public R myList() {
        List<ProcessInstance> list = processService.getMyProcessList();
        return R.ok(list);
    }

    /** 获取待我审批列表 */
    @GetMapping("/pending")
    public R pendingList() {
        List<ProcessInstance> list = processService.getPendingProcessList();
        return R.ok(list);
    }

    /** 获取流程详情 */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        ProcessInstance instance = processService.getProcessById(id);
        return R.ok(instance);
    }

    /** 获取审批记录 */
    @GetMapping("/{id}/records")
    public R records(@PathVariable Long id) {
        List<ApprovalRecord> list = processService.getApprovalRecords(id);
        return R.ok(list);
    }
}
