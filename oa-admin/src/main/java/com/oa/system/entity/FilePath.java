package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件路径实体
 */
@Data
@TableName("aoa_file_path")
public class FilePath implements Serializable {

    /**
     * 路径ID
     */
    @TableId(type = IdType.AUTO)
    private Long pathId;

    /**
     * 父路径ID
     */
    private Long parentId;

    /**
     * 路径名称
     */
    private String pathName;

    /**
     * 创建用户ID
     */
    private Long pathUserId;

    /**
     * 是否删除
     */
    private Integer pathIstrash;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子路径列表（非数据库字段）
     */
    @TableField(exist = false)
    private java.util.List<FilePath> children;
}
