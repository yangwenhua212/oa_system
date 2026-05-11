package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票记录实体
 */
@Data
@TableName("aoa_discuss_vote_record")
public class DiscussVoteRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 讨论ID
     */
    private Long discussId;

    /**
     * 选项ID
     */
    private Long voteItemId;

    /**
     * 投票用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
