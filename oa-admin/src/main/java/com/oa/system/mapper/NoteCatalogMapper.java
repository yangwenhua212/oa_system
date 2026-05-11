package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.NoteCatalog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 笔记分类Mapper接口
 */
@Mapper
public interface NoteCatalogMapper extends BaseMapper<NoteCatalog> {

    /**
     * 查询用户笔记分类列表
     */
    List<NoteCatalog> selectNoteCatalogList(Long userId);
}
