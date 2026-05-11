package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知公告评论实体
 */
@Data
@TableName("oa_notice_comment")
public class NoticeComment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人ID
     */
    private Long userId;

    /**
     * 评论人姓名
     */
    private String userName;

    /**
     * 回复目标评论ID
     */
    private Long replyId;

    /**
     * 状态: 1-正常, 0-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
