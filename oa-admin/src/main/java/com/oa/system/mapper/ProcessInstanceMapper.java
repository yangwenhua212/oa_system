package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.ProcessInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程实例Mapper接口
 */
@Mapper
public interface ProcessInstanceMapper extends BaseMapper<ProcessInstance> {

    /**
     * 查询流程实例列表
     */
    List<ProcessInstance> selectProcessInstanceList(ProcessInstance processInstance);

    /**
     * 查询我发起的流程
     */
    List<ProcessInstance> selectMyProcessList(@Param("userId") Long userId);

    /**
     * 查询待我审批的流程
     */
    List<ProcessInstance> selectPendingProcessList(@Param("approverId") Long approverId);
}
