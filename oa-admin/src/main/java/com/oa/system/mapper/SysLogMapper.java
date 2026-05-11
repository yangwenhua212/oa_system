package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 日志Mapper接口
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     * 查询日志列表
     */
    List<SysLog> selectLogList(SysLog sysLog);

    /**
     * 查询指定日期范围内每天的操作统计
     * 返回: [{date: '2026-05-04', loginCount: 5, actionCount: 12}, ...]
     */
    List<Map<String, Object>> selectDailyOperationStats(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
