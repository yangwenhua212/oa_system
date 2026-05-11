package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.entity.Plan;
import com.oa.system.mapper.PlanMapper;
import com.oa.system.service.PlanService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 计划服务实现
 */
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public List<Plan> getMyPlanList(Long userId) {
        return planMapper.selectByUserId(userId);
    }

    @Override
    public List<Plan> getAllPlanList() {
        return planMapper.selectAllList();
    }

    @Override
    public Plan getPlanById(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public Long addPlan(Plan plan) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        plan.setPlanUserId(userId);
        planMapper.insert(plan);
        return plan.getPlanId();
    }

    @Override
    public boolean updatePlan(Plan plan) {
        planMapper.updateById(plan);
        return true;
    }

    @Override
    public boolean deletePlan(Long id) {
        planMapper.deleteById(id);
        return true;
    }
}
