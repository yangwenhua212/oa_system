package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 群聊实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_chat_group")
public class ChatGroup extends BaseEntity implements Serializable {

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 群头像
     */
    private String avatar;

    /**
     * 群主ID
     */
    private Long ownerId;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 最大成员数
     */
    private Integer maxMember;

    /**
     * 当前成员数
     */
    private Integer memberCount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 成员列表（非数据库字段）
     */
    @TableField(exist = false)
    private Object members;
}
