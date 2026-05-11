package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.FilePath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件路径Mapper接口
 */
@Mapper
public interface FilePathMapper extends BaseMapper<FilePath> {

    /**
     * 根据父路径查询子路径
     */
    @Select("SELECT * FROM aoa_file_path WHERE parent_id = #{parentId} AND path_istrash = 0 ORDER BY path_id")
    List<FilePath> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询用户根路径
     */
    @Select("SELECT * FROM aoa_file_path WHERE path_user_id = #{userId} AND parent_id IS NULL AND path_istrash = 0")
    List<FilePath> selectRootByUserId(@Param("userId") Long userId);
}
