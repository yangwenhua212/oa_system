package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class DictType extends BaseEntity implements Serializable {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
