package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通讯录分享实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_address_share")
public class AddressShare extends BaseEntity {

    /**
     * 联系人ID
     */
    private Long contactId;

    /**
     * 分享者用户ID
     */
    private Long sharedBy;

    /**
     * 被分享用户ID
     */
    private Long sharedWith;

    /**
     * 分享者姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String sharedByName;

    /**
     * 被分享者姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String sharedWithName;

    /**
     * 联系人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String name;

    /**
     * 联系人性别（非数据库字段）
     */
    @TableField(exist = false)
    private String sex;

    /**
     * 联系人电话（非数据库字段）
     */
    @TableField(exist = false)
    private String phone;

    /**
     * 联系人邮箱（非数据库字段）
     */
    @TableField(exist = false)
    private String email;

    /**
     * 联系公司（非数据库字段）
     */
    @TableField(exist = false)
    private String company;
}
