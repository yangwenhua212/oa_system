package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Overtime;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 加班Mapper接口
 */
@Mapper
public interface OvertimeMapper extends BaseMapper<Overtime> {

    /**
     * 查询加班列表
     */
    List<Overtime> selectOvertimeList(Overtime overtime);

    /**
     * 查询我的加班列表
     */
    List<Overtime> selectMyOvertimeList(Long userId);
}
