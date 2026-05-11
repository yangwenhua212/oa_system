package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讨论回复实体
 */
@Data
@TableName("aoa_discuss_reply")
public class DiscussReply implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 讨论ID
     */
    private Long discussId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 回复用户ID
     */
    private Long userId;

    /**
     * 回复用户名
     */
    private String userName;

    /**
     * 父回复ID（回复某条回复时）
     */
    private Long replyId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
