package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 通讯录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_address_book")
public class AddressBook extends BaseEntity implements Serializable {

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人类型: personal-个人, company-公司
     */
    private String contactType;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 地址
     */
    private String address;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 微信
     */
    private String wechat;

    /**
     * QQ
     */
    private String qq;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否星标
     */
    private Integer star;

    /**
     * 分组ID
     */
    private Long groupId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 性别（非数据库字段）
     */
    @TableField(exist = false)
    private String sex;

    /**
     * 头像图片（非数据库字段）
     */
    @TableField(exist = false)
    private String image;

    /**
     * 分组名称（非数据库字段）
     */
    @TableField(exist = false)
    private String groupName;
}
