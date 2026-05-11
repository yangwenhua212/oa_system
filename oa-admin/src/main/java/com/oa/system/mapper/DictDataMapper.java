package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据Mapper接口
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    /**
     * 查询字典数据列表
     */
    List<DictData> selectDictDataList(DictData dictData);

    /**
     * 根据字典类型查询字典数据
     */
    List<DictData> selectDictDataByType(@Param("dictType") String dictType);
}
