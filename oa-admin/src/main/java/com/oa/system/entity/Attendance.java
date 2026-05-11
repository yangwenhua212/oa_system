package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa_attendance")
public class Attendance extends BaseEntity implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 上班打卡时间
     */
    private LocalDateTime clockInTime;

    /**
     * 下班打卡时间
     */
    private LocalDateTime clockOutTime;

    /**
     * 上班状态: normal-正常, late-迟到, absent-缺勤
     */
    private String clockInStatus;

    /**
     * 下班状态: normal-正常, leave-早退, absent-缺勤
     */
    private String clockOutStatus;

    /**
     * 工作时长(小时)
     */
    private BigDecimal workDuration;

    /**
     * 加班时长(小时)
     */
    private BigDecimal overtimeDuration;

    /**
     * 备注
     */
    private String remark;

    // ========== 显式 getter/setter ==========

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}
