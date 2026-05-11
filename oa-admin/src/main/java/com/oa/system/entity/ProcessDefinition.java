package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_process_definition")
public class ProcessDefinition extends BaseEntity implements Serializable {

    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程类型
     */
    private String processType;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 流程配置JSON
     */
    private String flowData;
}
