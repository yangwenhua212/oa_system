package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 日程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_schedule")
public class Schedule extends BaseEntity implements Serializable {

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 日程标题
     */
    private String title;

    /**
     * 日程内容
     */
    private String content;

    /**
     * 地点
     */
    private String location;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否全天
     */
    private Integer allDay;

    /**
     * 提醒类型: none-不提醒, 5-提前5分钟, 15-提前15分钟, 30-提前30分钟, 60-提前1小时, 1day-提前1天
     */
    private String remindType;

    /**
     * 日程颜色
     */
    private String color;

    /**
     * 重复类型: none-不重复, daily-每天, weekly-每周, monthly-每月, yearly-每年
     */
    private String repeatType;

    /**
     * 隐私类型: public-公开, private-私密
     */
    private String privacyType;

    /**
     * 状态
     */
    private Integer status;
}
