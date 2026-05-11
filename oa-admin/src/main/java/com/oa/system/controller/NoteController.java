package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Note;
import com.oa.system.entity.NoteCatalog;
import com.oa.system.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 笔记管理控制器
 */
@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * 创建笔记
     */
    @PostMapping
    public R create(@RequestBody Note note) {
        Long id = noteService.createNote(note);
        return R.ok(id);
    }

    /**
     * 更新笔记
     */
    @PutMapping
    public R update(@RequestBody Note note) {
        noteService.updateNote(note);
        return R.ok();
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        noteService.deleteNote(id);
        return R.ok();
    }

    /**
     * 获取笔记详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        return R.ok(note);
    }

    /**
     * 分页查询笔记列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Note> pageDTO) {
        PageR<Note> result = noteService.getNotePage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的笔记列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Note> list = noteService.getMyNoteList();
        return R.ok(list);
    }

    /**
     * 获取笔记分类
     */
    @GetMapping("/catalog")
    public R catalog() {
        List<NoteCatalog> list = noteService.getCatalogList();
        return R.ok(list);
    }

    /**
     * 创建笔记分类
     */
    @PostMapping("/catalog")
    public R createCatalog(@RequestBody NoteCatalog catalog) {
        Long id = noteService.createCatalog(catalog);
        return R.ok(id);
    }

    /**
     * 删除笔记分类
     */
    @DeleteMapping("/catalog/{id}")
    public R deleteCatalog(@PathVariable Long id) {
        noteService.deleteCatalog(id);
        return R.ok();
    }
}
