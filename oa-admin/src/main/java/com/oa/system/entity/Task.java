package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_task")
public class Task extends BaseEntity implements Serializable {

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 优先级: low-低, medium-中, high-高, urgent-紧急
     */
    private String priority;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    private LocalDateTime endTime;

    /**
     * 状态: todo-待处理, in_progress-进行中, testing-测试中, completed-已完成, closed-已关闭
     */
    private String status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 预计工时
     */
    private BigDecimal estimatedHours;

    /**
     * 已用工时
     */
    private BigDecimal spentHours;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 负责人ID
     */
    private Long handlerId;

    /**
     * 负责人姓名
     */
    private String handlerName;

    /**
     * 所属部门
     */
    private Long deptId;

    /**
     * 附件ID列表
     */
    private String attachmentIds;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
