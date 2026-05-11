package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Note;
import com.oa.system.entity.NoteCatalog;

import java.util.List;

/**
 * 笔记服务接口
 */
public interface NoteService {

    /**
     * 创建笔记
     */
    Long createNote(Note note);

    /**
     * 更新笔记
     */
    boolean updateNote(Note note);

    /**
     * 删除笔记
     */
    boolean deleteNote(Long id);

    /**
     * 获取笔记详情
     */
    Note getNoteById(Long id);

    /**
     * 分页查询笔记列表
     */
    PageR<Note> getNotePage(PageDTO<Note> pageDTO);

    /**
     * 获取我的笔记列表
     */
    List<Note> getMyNoteList();

    /**
     * 获取笔记分类
     */
    List<NoteCatalog> getCatalogList();

    /**
     * 创建笔记分类
     */
    Long createCatalog(NoteCatalog catalog);

    /**
     * 删除笔记分类
     */
    boolean deleteCatalog(Long id);
}
