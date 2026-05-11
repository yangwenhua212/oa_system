package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通讯录联系人实体
 */
@Data
@TableName("aoa_director")
public class Director implements Serializable {

    /**
     * 联系人ID
     */
    @TableId(type = IdType.AUTO)
    private Long directorId;

    /**
     * 地址
     */
    private String address;

    /**
     * 公司电话
     */
    private String companyNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像ID
     */
    private Long imagePath;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 备注
     */
    private String remark;

    /**
     * 性别
     */
    private String sex;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 公司名称
     */
    private String companyname;

    /**
     * 所属用户（非数据库字段）
     */
    @TableField(exist = false)
    private String creatorName;
}
