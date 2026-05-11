package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Position;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 职位Mapper接口
 */
@Mapper
public interface PositionMapper extends BaseMapper<Position> {

    /**
     * 查询职位列表
     */
    List<Position> selectPositionList(Position position);
}
