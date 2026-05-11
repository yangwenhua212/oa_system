package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Notice;
import com.oa.system.entity.NoticeComment;
import com.oa.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告控制器
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 发布通知
     */
    @PostMapping
    public R publish(@RequestBody Notice notice) {
        Long id = noticeService.publishNotice(notice);
        return R.ok(id);
    }

    /**
     * 更新通知
     */
    @PutMapping
    public R update(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return R.ok();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return R.ok();
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        return R.ok(notice);
    }

    /**
     * 分页查询通知列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Notice> pageDTO) {
        PageR<Notice> result = noticeService.getNoticePage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的通知列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Notice> list = noticeService.getMyNoticeList();
        return R.ok(list);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread")
    public R unreadCount() {
        Long count = noticeService.getUnreadCount();
        return R.ok(count);
    }

    /**
     * 标记通知已读
     */
    @PutMapping("/read/{id}")
    public R markAsRead(@PathVariable Long id) {
        noticeService.markAsRead(id);
        return R.ok();
    }

    // ====== 评论 ======

    /**
     * 获取公告评论
     */
    @GetMapping("/comment/{noticeId}")
    public R commentList(@PathVariable Long noticeId) {
        List<NoticeComment> list = noticeService.getCommentList(noticeId);
        return R.ok(list);
    }

    /**
     * 添加评论
     */
    @PostMapping("/comment")
    public R addComment(@RequestBody NoticeComment comment) {
        Long id = noticeService.addComment(comment);
        return R.ok(id);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{id}")
    public R deleteComment(@PathVariable Long id) {
        noticeService.deleteComment(id);
        return R.ok();
    }
}
