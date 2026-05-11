package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件Mapper接口
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    /**
     * 根据路径ID查询文件列表
     */
    @Select("SELECT * FROM aoa_file_list WHERE path_id = #{pathId} AND file_istrash = 0 ORDER BY upload_time DESC")
    List<File> selectByPathId(@Param("pathId") Long pathId);

    /**
     * 查询用户上传的文件列表
     */
    @Select("SELECT * FROM aoa_file_list WHERE file_user_id = #{userId} AND file_istrash = 0 ORDER BY upload_time DESC")
    List<File> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询回收站文件
     */
    @Select("SELECT * FROM aoa_file_list WHERE file_user_id = #{userId} AND file_istrash = 1 ORDER BY upload_time DESC")
    List<File> selectTrashByUserId(@Param("userId") Long userId);
}
