package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知公告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_notice")
public class Notice extends BaseEntity implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型: normal-普通, important-重要, urgent-紧急
     */
    private String noticeType;

    /**
     * 发布范围: all-全体, dept-部门, user-个人
     */
    private String publishScope;

    /**
     * 目标部门ID列表
     */
    private String targetDeptIds;

    /**
     * 目标用户ID列表
     */
    private String targetUserIds;

    /**
     * 附件ID列表
     */
    private String attachmentIds;

    /**
     * 发布人ID
     */
    private Long publisherId;

    /**
     * 发布人姓名
     */
    private String publisherName;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否公开
     */
    private Integer isPublic;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 附件列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Map<String, Object>> attachments;
}
