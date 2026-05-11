package com.oa.system.service;

import com.oa.system.entity.Plan;

import java.util.List;

/**
 * 计划服务接口
 */
public interface PlanService {

    /**
     * 获取我的计划列表
     */
    List<Plan> getMyPlanList(Long userId);

    /**
     * 获取所有计划列表（管理员）
     */
    List<Plan> getAllPlanList();

    /**
     * 根据ID查询
     */
    Plan getPlanById(Long id);

    /**
     * 新增计划
     */
    Long addPlan(Plan plan);

    /**
     * 修改计划
     */
    boolean updatePlan(Plan plan);

    /**
     * 删除计划
     */
    boolean deletePlan(Long id);
}
