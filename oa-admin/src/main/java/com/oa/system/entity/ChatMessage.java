package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
@TableName("oa_chat_message")
public class ChatMessage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 群ID
     */
    private Long groupId;

    /**
     * 发送人ID
     */
    private Long userId;

    /**
     * 发送人姓名
     */
    private String userName;

    /**
     * 发送人头像
     */
    private String userAvatar;

    /**
     * 消息类型: text-文本, image-图片, file-文件, emoji-表情
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件大小
     */
    private Long attachmentSize;

    /**
     * 回复ID
     */
    private Long replyId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
