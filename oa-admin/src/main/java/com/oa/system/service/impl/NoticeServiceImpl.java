package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.File;
import com.oa.system.entity.Notice;
import com.oa.system.entity.NoticeComment;
import com.oa.system.entity.User;
import com.oa.system.mapper.FileMapper;
import com.oa.system.mapper.NoticeCommentMapper;
import com.oa.system.mapper.NoticeMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.NoticeService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final UserMapper userMapper;
    private final NoticeCommentMapper noticeCommentMapper;
    private final FileMapper fileMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public Long publishNotice(Notice notice) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        notice.setPublisherId(userId);
        notice.setPublisherName(user.getRealName());
        notice.setPublishTime(LocalDateTime.now());
        notice.setViewCount(0);
        notice.setDelFlag(0);

        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public boolean updateNotice(Notice notice) {
        noticeMapper.updateById(notice);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteNotice(Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Notice notice = noticeMapper.selectById(id);

        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        if (!userId.equals(notice.getPublisherId())) {
            throw new RuntimeException("只能删除自己发布的通知");
        }

        noticeMapper.deleteById(id);
        return true;
    }

    @Override
    public Notice getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            // 更新浏览次数
            noticeMapper.updateViewCount(id);

            // 标记已读
            markAsRead(id);

            // 查询附件信息
            if (notice.getAttachmentIds() != null && !notice.getAttachmentIds().isEmpty()) {
                List<Long> ids = Arrays.stream(notice.getAttachmentIds().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                if (!ids.isEmpty()) {
                    List<File> files = fileMapper.selectBatchIds(ids);
                    List<Map<String, Object>> attachments = new ArrayList<>();
                    for (File f : files) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("fileId", f.getFileId());
                        item.put("name", f.getFileName());
                        item.put("size", f.getSize());
                        item.put("filePath", f.getFilePath());
                        attachments.add(item);
                    }
                    notice.setAttachments(attachments);
                }
            }
        }
        return notice;
    }

    @Override
    public PageR<Notice> getNoticePage(PageDTO<Notice> pageDTO) {
        Page<Notice> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Notice params = pageDTO.getParams() != null ? pageDTO.getParams() : new Notice();

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getTitle() != null, Notice::getTitle, params.getTitle())
                .eq(params.getNoticeType() != null, Notice::getNoticeType, params.getNoticeType())
                .eq(params.getStatus() != null, Notice::getStatus, params.getStatus())
                .orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getPublishTime);

        Page<Notice> result = noticeMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Notice> getMyNoticeList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return noticeMapper.selectMyNoticeList(userId);
    }

    @Override
    public Long getUnreadCount() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Long count = noticeMapper.countUnread(userId);
        return count != null ? count : 0L;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long noticeId) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        noticeMapper.insertRead(noticeId, userId);
        return true;
    }

    // ====== 评论 ======

    @Override
    public List<NoticeComment> getCommentList(Long noticeId) {
        return noticeCommentMapper.selectByNoticeId(noticeId);
    }

    @Override
    @Transactional
    public Long addComment(NoticeComment comment) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        comment.setUserId(userId);
        comment.setUserName(user.getRealName());
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());

        noticeCommentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id) {
        NoticeComment comment = noticeCommentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus(0);
            noticeCommentMapper.updateById(comment);
        }
        return true;
    }
}
