package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends BaseEntity implements Serializable {

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型: M-目录, C-菜单, F-按钮
     */
    private String menuType;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否外链: 0-是, 1-否
     */
    private Integer isFrame;

    /**
     * 是否缓存: 0-缓存, 1-不缓存
     */
    private Integer isCache;

    /**
     * 显示状态: 0-隐藏, 1-显示
     */
    private Integer visible;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子菜单（非数据库字段）
     */
    @TableField(exist = false)
    private List<Menu> children;

    // ========== 显式 getter/setter ==========
    
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }
}
