package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 部门实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class Dept extends BaseEntity implements Serializable {

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门类型
     */
    private String deptType;

    /**
     * 负责人ID
     */
    private Long leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子部门（非数据库字段）
     */
    @TableField(exist = false)
    private List<Dept> children;

    /**
     * 负责人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String leaderName;
}
