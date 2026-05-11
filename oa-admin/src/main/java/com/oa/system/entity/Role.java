package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
public class Role implements Serializable {

    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色类型
     */
    private String roleType;

    /**
     * 数据范围: 1-全部, 2-本部门, 3-本部门及以下, 4-仅本人
     */
    private Integer dataScope;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 菜单ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private Long[] menuIds;

    /**
     * 菜单数量（非数据库字段，用于列表显示）
     */
    @TableField(exist = false)
    private Integer menuCount;
}
