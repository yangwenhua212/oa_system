package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程实例实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_process_instance")
public class ProcessInstance extends BaseEntity implements Serializable {

    /**
     * 流程定义ID
     */
    private Long processDefinitionId;

    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 当前节点Key
     */
    private String currentNodeKey;

    /**
     * 当前节点名称
     */
    private String currentNodeName;

    /**
     * 当前审批人ID
     */
    private Long currentApproverId;

    /**
     * 当前审批人姓名
     */
    private String currentApproverName;

    /**
     * 表单数据JSON
     */
    private String formData;

    /**
     * 状态: pending-审批中, approved-已通过, rejected-已拒绝, cancelled-已撤回
     */
    private String status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 处理时长(秒)
     */
    private Integer duration;
}
