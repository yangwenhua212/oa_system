package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Leave;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 请假Mapper接口
 */
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {

    /**
     * 查询请假列表
     */
    List<Leave> selectLeaveList(Leave leave);

    /**
     * 查询我的请假列表
     */
    List<Leave> selectMyLeaveList(Long userId);
}
