package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.ProcessDefinition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程定义Mapper接口
 */
@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinition> {

    /**
     * 查询流程定义列表
     */
    List<ProcessDefinition> selectProcessDefinitionList(ProcessDefinition processDefinition);
}
