package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据实体
 */
@Data
@TableName("sys_dict_data")
public class DictData implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型ID
     */
    private Long dictTypeId;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 数据类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 字典名称（非数据库字段）
     */
    @TableField(exist = false)
    private String dictName;
}
