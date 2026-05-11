package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮件账号实体
 */
@Data
@TableName("oa_mail_account")
public class MailAccount implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号类型: system-系统邮件 */
    private String accountType;

    /** 账号名称 */
    private String accountName;

    /** 账号地址 */
    private String accountAddr;

    /** 发件昵称 */
    private String senderNickname;

    /** 授权码 */
    private String authCode;

    /** 备注 */
    private String remark;

    /** 状态: 0-禁用, 1-有效 */
    private Integer status;

    /** 创建用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
