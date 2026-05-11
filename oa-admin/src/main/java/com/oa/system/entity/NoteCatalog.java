package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 笔记分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_note_catalog")
public class NoteCatalog extends BaseEntity implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分类名称
     */
    private String catalogName;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;
}
