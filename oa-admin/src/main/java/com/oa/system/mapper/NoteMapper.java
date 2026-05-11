package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔记Mapper接口
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    /**
     * 查询笔记列表
     */
    List<Note> selectNoteList(Note note);

    /**
     * 查询我的笔记
     */
    List<Note> selectMyNoteList(@Param("userId") Long userId);
}
