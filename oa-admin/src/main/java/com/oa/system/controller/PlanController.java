package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.Plan;
import com.oa.system.service.PlanService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作计划控制器
 */
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 获取计划列表
     */
    @GetMapping("/list")
    public R list() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<Plan> list = planService.getMyPlanList(userId);
        return R.ok(list);
    }

    /**
     * 获取计划类型
     */
    @GetMapping("/types")
    public R types() {
        Map<String, String> types = new HashMap<>();
        types.put("1", "工作");
        types.put("2", "学习");
        types.put("3", "生活");
        types.put("4", "其他");
        return R.ok(types);
    }

    /**
     * 获取计划状态
     */
    @GetMapping("/statuses")
    public R statuses() {
        Map<String, String> statuses = new HashMap<>();
        statuses.put("1", "未开始");
        statuses.put("2", "进行中");
        statuses.put("3", "已完成");
        statuses.put("4", "已暂停");
        return R.ok(statuses);
    }

    /**
     * 获取计划详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Plan plan = planService.getPlanById(id);
        return R.ok(plan);
    }

    /**
     * 新增计划
     */
    @PostMapping
    public R add(@RequestBody Plan plan) {
        Long id = planService.addPlan(plan);
        return R.ok(id);
    }

    /**
     * 修改计划
     */
    @PutMapping
    public R update(@RequestBody Plan plan) {
        planService.updatePlan(plan);
        return R.ok();
    }

    /**
     * 删除计划
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        planService.deletePlan(id);
        return R.ok();
    }
}
