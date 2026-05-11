package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讨论区实体
 */
@Data
@TableName("aoa_discuss_list")
public class Discuss implements Serializable {

    /**
     * 讨论ID
     */
    @TableId(type = IdType.AUTO)
    private Long discussId;

    /**
     * 附件ID
     */
    private Long attachmentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
     * 浏览次数
     */
    private Integer visitNum;

    /**
     * 发布用户ID
     */
    private Long discussUserId;

    /**
     * 投票ID
     */
    private Long voteId;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 发布用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String discussUserName;

    /**
     * 发布用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String discussUserAvatar;

    /**
     * 回复数（非数据库字段）
     */
    @TableField(exist = false)
    private Integer replyCount;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
