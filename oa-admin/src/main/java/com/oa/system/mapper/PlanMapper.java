package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 计划Mapper接口
 */
@Mapper
public interface PlanMapper extends BaseMapper<Plan> {

    /**
     * 查询用户的计划列表
     */
    @Select("SELECT p.* FROM aoa_plan_list p WHERE p.plan_user_id = #{userId} ORDER BY p.start_time DESC")
    List<Plan> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有计划（管理员）
     */
    @Select("SELECT p.* FROM aoa_plan_list p ORDER BY p.start_time DESC")
    List<Plan> selectAllList();
}
