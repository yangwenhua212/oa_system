package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Notice;
import com.oa.system.entity.NoticeComment;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NoticeService {

    /**
     * 发布通知
     */
    Long publishNotice(Notice notice);

    /**
     * 更新通知
     */
    boolean updateNotice(Notice notice);

    /**
     * 删除通知
     */
    boolean deleteNotice(Long id);

    /**
     * 获取通知详情
     */
    Notice getNoticeById(Long id);

    /**
     * 分页查询通知列表
     */
    PageR<Notice> getNoticePage(PageDTO<Notice> pageDTO);

    /**
     * 获取我的通知列表
     */
    List<Notice> getMyNoticeList();

    /**
     * 获取未读通知数量
     */
    Long getUnreadCount();

    /**
     * 标记通知已读
     */
    boolean markAsRead(Long noticeId);

    // ====== 评论 ======

    /**
     * 获取公告评论列表
     */
    List<NoticeComment> getCommentList(Long noticeId);

    /**
     * 添加评论
     */
    Long addComment(NoticeComment comment);

    /**
     * 删除评论
     */
    boolean deleteComment(Long id);
}
