package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.DictType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典类型Mapper接口
 */
@Mapper
public interface DictTypeMapper extends BaseMapper<DictType> {

    /**
     * 查询字典类型列表
     */
    List<DictType> selectDictTypeList(DictType dictType);
}
