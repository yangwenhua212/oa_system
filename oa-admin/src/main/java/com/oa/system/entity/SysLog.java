package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_log")
public class SysLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 状态: 0-异常, 1-正常
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时长(毫秒)
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
