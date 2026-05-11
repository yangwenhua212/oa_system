package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 请假实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_leave")
public class Leave extends BaseEntity implements Serializable {

    /**
     * 申请人ID
     */
    private Long userId;

    /**
     * 申请人用户名
     */
    private String username;

    /**
     * 申请人姓名
     */
    private String realName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 请假类型: annual-年假, sick-病假, personal-事假, marriage-婚假, maternity-产假, funeral-丧假
     */
    private String leaveType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 时长(天)
     */
    private BigDecimal duration;

    /**
     * 请假原因
     */
    private String reason;

    /**
     * 流程实例ID
     */
    private Long processInstanceId;

    /**
     * 状态: draft-草稿, pending-审批中, approved-已通过, rejected-已拒绝, cancelled-已取消
     */
    private String status;

    /**
     * 当前审批人
     */
    private Long currentApprover;

    /**
     * 审批人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String currentApproverName;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
