package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_mail")
public class Mail extends BaseEntity implements Serializable {

    /**
     * 邮件唯一标识
     */
    private String mailId;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 文件夹: inbox-收件箱, sent-已发送, draft-草稿箱, trash-回收站
     */
    private String folder;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 是否星标
     */
    private Integer isStar;

    /**
     * 是否重要
     */
    private Integer isImportant;

    /**
     * 发件人ID
     */
    private Long senderId;

    /**
     * 发件人
     */
    private String senderName;

    /**
     * 发件人邮箱
     */
    private String senderEmail;

    /**
     * 收件人ID列表
     */
    private String toIds;

    /**
     * 收件人姓名列表
     */
    private String toNames;

    /**
     * 收件人邮箱列表
     */
    private String toEmails;

    /**
     * 抄送人ID列表
     */
    private String ccIds;

    /**
     * 抄送人姓名列表
     */
    private String ccNames;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 纯文本内容
     */
    private String contentText;

    /**
     * 附件ID列表
     */
    private String attachmentIds;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
}
