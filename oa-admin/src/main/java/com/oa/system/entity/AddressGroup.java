package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 通讯录分组实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_address_group")
public class AddressGroup extends BaseEntity implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组类型
     */
    private String groupType;

    /**
     * 排序
     */
    private Integer sort;
}
