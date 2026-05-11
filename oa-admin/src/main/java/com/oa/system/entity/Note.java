package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 笔记实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_note")
public class Note extends BaseEntity implements Serializable {

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容
     */
    private String content;

    /**
     * 纯文本内容
     */
    private String contentText;

    /**
     * 分类ID
     */
    private Long catalogId;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 背景颜色
     */
    private String color;

    /**
     * 是否公开
     */
    private Integer isPublic;

    /**
     * 分享用户ID列表
     */
    private String shareUserIds;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String catalogName;
}
