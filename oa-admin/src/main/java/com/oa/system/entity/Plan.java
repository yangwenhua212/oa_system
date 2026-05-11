package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 计划实体
 */
@Data
@TableName("aoa_plan_list")
public class Plan implements Serializable {

    /**
     * 计划ID
     */
    @TableId(type = IdType.AUTO)
    private Long planId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 标签
     */
    private String label;

    /**
     * 计划评论
     */
    private String planComment;

    /**
     * 计划内容
     */
    private String planContent;

    /**
     * 计划摘要
     */
    private String planSummary;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 状态ID
     */
    private Long statusId;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 创建用户ID
     */
    private Long planUserId;

    /**
     * 附件ID
     */
    private Long attachId;

    /**
     * 创建用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String planUserName;

    /**
     * 状态名称（非数据库字段）
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 类型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String typeName;
}
