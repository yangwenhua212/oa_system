package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务评论实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_task_comment")
public class TaskComment extends BaseEntity implements Serializable {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 回复ID
     */
    private Long replyId;

    /**
     * 评论人ID
     */
    private Long userId;

    /**
     * 评论人姓名
     */
    private String userName;

    /**
     * 评论人头像
     */
    private String userAvatar;

    /**
     * 状态
     */
    private Integer status;
}
