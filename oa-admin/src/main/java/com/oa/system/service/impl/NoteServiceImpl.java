package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Note;
import com.oa.system.entity.NoteCatalog;
import com.oa.system.mapper.NoteCatalogMapper;
import com.oa.system.mapper.NoteMapper;
import com.oa.system.service.NoteService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 笔记服务实现类
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;
    private final NoteCatalogMapper noteCatalogMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Long createNote(Note note) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        note.setUserId(userId);
        note.setStatus(1);
        noteMapper.insert(note);
        return note.getId();
    }

    @Override
    public boolean updateNote(Note note) {
        noteMapper.updateById(note);
        return true;
    }

    @Override
    public boolean deleteNote(Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Note note = noteMapper.selectById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        if (!userId.equals(note.getUserId())) {
            throw new RuntimeException("只能删除自己的笔记");
        }
        noteMapper.deleteById(id);
        return true;
    }

    @Override
    public Note getNoteById(Long id) {
        return noteMapper.selectById(id);
    }

    @Override
    public PageR<Note> getNotePage(PageDTO<Note> pageDTO) {
        Page<Note> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Note params = pageDTO.getParams() != null ? pageDTO.getParams() : new Note();

        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getTitle() != null, Note::getTitle, params.getTitle())
                .orderByDesc(Note::getUpdateTime);

        Page<Note> result = noteMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Note> getMyNoteList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return noteMapper.selectMyNoteList(userId);
    }

    @Override
    public List<NoteCatalog> getCatalogList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return noteCatalogMapper.selectNoteCatalogList(userId);
    }

    @Override
    public Long createCatalog(NoteCatalog catalog) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        catalog.setUserId(userId);
        noteCatalogMapper.insert(catalog);
        return catalog.getId();
    }

    @Override
    public boolean deleteCatalog(Long id) {
        noteCatalogMapper.deleteById(id);
        return true;
    }
}
