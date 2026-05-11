package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票选项实体
 */
@Data
@TableName("aoa_discuss_vote_item")
public class DiscussVoteItem implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 讨论ID
     */
    private Long discussId;

    /**
     * 选项内容
     */
    private String content;

    /**
     * 得票数
     */
    private Integer voteCount;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
