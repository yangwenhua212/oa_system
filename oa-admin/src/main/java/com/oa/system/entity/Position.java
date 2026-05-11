package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 职位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_position")
public class Position extends BaseEntity implements Serializable {

    /**
     * 职位名称
     */
    private String positionName;

    /**
     * 职位编码
     */
    private String positionCode;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 薪资级别
     */
    private String salaryLevel;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
