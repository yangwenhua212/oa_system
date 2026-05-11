package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讨论点赞实体
 */
@Data
@TableName("aoa_discuss_like")
public class DiscussLike implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID（点赞帖子时）
     */
    private Long discussId;

    /**
     * 回复ID（点赞回复时）
     */
    private Long replyId;

    /**
     * 点赞用户ID
     */
    private Long userId;

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
