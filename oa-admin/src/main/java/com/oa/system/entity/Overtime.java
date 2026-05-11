package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 加班实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_overtime")
public class Overtime extends BaseEntity implements Serializable {

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
     * 加班开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 加班结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 加班时长(小时)
     */
    private BigDecimal duration;

    /**
     * 加班原因
     */
    private String reason;

    /**
     * 加班类型: weekday-工作日, weekend-周末, holiday-节假日
     */
    private String overtimeType;

    /**
     * 状态
     */
    private String status;

    /**
     * 流程实例ID
     */
    private Long processInstanceId;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
