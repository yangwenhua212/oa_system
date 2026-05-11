package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别: 0-未知, 1-男, 2-女
     */
    private Integer sex;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 职位ID
     */
    private Long positionId;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 是否管理员: 0-否, 1-是
     */
    private Integer isAdmin;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 地址（暂未入库）
     */
    @TableField(exist = false)
    private String address;

    /**
     * 毕业院校（暂未入库）
     */
    @TableField(exist = false)
    private String school;

    /**
     * 银行账号（暂未入库）
     */
    @TableField(exist = false)
    private String bankAccount;

    /**
     * 学历（暂未入库）
     */
    @TableField(exist = false)
    private String education;

    /**
     * 身份证号（暂未入库）
     */
    @TableField(exist = false)
    private String idCard;

    /**
     * 工资（暂未入库）
     */
    @TableField(exist = false)
    private java.math.BigDecimal salary;

    /**
     * 皮肤（暂未入库）
     */
    @TableField(exist = false)
    private String skin;

    /**
     * 角色ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private Long[] roleIds;

    /**
     * 角色名称（非数据库字段，逗号分隔）
     */
    @TableField(exist = false)
    private String roleNames;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 职位名称（非数据库字段）
     */
    @TableField(exist = false)
    private String positionName;

}
