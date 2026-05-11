package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件实体
 */
@Data
@TableName("aoa_file_list")
public class File implements Serializable {

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件后缀
     */
    private String fileShuffix;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 模块
     */
    private String model;

    /**
     * 路径ID
     */
    private Long pathId;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传用户ID
     */
    private Long fileUserId;

    /**
     * 是否删除
     */
    private Integer fileIstrash;

    /**
     * 是否共享
     */
    private Integer fileIsshare;

    /**
     * 上传用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String fileUserName;

    /**
     * 所属路径名（非数据库字段）
     */
    @TableField(exist = false)
    private String pathName;
}
