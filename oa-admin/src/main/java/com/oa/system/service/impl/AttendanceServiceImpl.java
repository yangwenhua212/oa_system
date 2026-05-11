package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Attendance;
import com.oa.system.entity.Dept;
import com.oa.system.entity.User;
import com.oa.system.mapper.AttendanceMapper;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.AttendanceService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现类
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Attendance clockIn() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);
        LocalDate today = LocalDate.now();

        // 检查是否已打卡
        Attendance exist = attendanceMapper.selectAttendanceByUserAndDate(userId, today);
        if (exist != null && exist.getClockInTime() != null) {
            throw new RuntimeException("今日已签到");
        }

        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setUsername(user.getUsername());
        attendance.setRealName(user.getRealName());
        attendance.setAttendanceDate(today);
        attendance.setClockInTime(LocalDateTime.now());

        // 判断上班状态（9:00为标准）
        LocalDateTime standardTime = today.atTime(9, 0);
        if (LocalDateTime.now().isAfter(standardTime)) {
            attendance.setClockInStatus("late");
        } else {
            attendance.setClockInStatus("normal");
        }

        if (user.getDeptId() != null) {
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                attendance.setDeptId(dept.getId());
                attendance.setDeptName(dept.getDeptName());
            }
        }

        attendanceMapper.insert(attendance);
        return attendance;
    }

    @Override
    public Attendance clockOut() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceMapper.selectAttendanceByUserAndDate(userId, today);
        if (attendance == null) {
            throw new RuntimeException("今日未签到，无法签退");
        }

        if (attendance.getClockOutTime() != null) {
            throw new RuntimeException("今日已签退");
        }

        attendance.setClockOutTime(LocalDateTime.now());

        // 判断下班状态（18:00为标准）
        LocalDateTime standardTime = today.atTime(18, 0);
        if (LocalDateTime.now().isBefore(standardTime)) {
            attendance.setClockOutStatus("early");
        } else {
            attendance.setClockOutStatus("normal");
        }

        // 计算工作时长
        if (attendance.getClockInTime() != null && attendance.getClockOutTime() != null) {
            long minutes = java.time.Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toMinutes();
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            attendance.setWorkDuration(hours);
        }

        attendanceMapper.updateById(attendance);
        return attendance;
    }

    @Override
    public Attendance getTodayAttendance() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return attendanceMapper.selectAttendanceByUserAndDate(userId, LocalDate.now());
    }

    @Override
    public PageR<Attendance> getAttendancePage(PageDTO<Attendance> pageDTO,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               String realName,
                                               String clockInStatus) {
        Page<Attendance> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, Attendance::getAttendanceDate, startDate)
                .le(endDate != null, Attendance::getAttendanceDate, endDate)
                .like(realName != null && !realName.isEmpty(), Attendance::getRealName, realName)
                .eq(clockInStatus != null && !clockInStatus.isEmpty(), Attendance::getClockInStatus, clockInStatus)
                .orderByDesc(Attendance::getAttendanceDate);

        Page<Attendance> result = attendanceMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public Map<String, Object> getAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null) {
            userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        }
        List<Attendance> records = attendanceMapper.selectAttendanceStatistics(userId, startDate, endDate);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total", records.size());
        statistics.put("normal", records.stream().filter(a -> "normal".equals(a.getClockInStatus()) && "normal".equals(a.getClockOutStatus())).count());
        statistics.put("late", records.stream().filter(a -> "late".equals(a.getClockInStatus())).count());
        statistics.put("early", records.stream().filter(a -> "early".equals(a.getClockOutStatus())).count());
        statistics.put("records", records);

        return statistics;
    }

    @Override
    public List<Attendance> getAttendanceCalendar(LocalDate startDate, LocalDate endDate) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return attendanceMapper.selectAttendanceStatistics(userId, startDate, endDate);
    }
}
