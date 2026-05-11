package com.oa.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Mail;
import com.oa.system.entity.User;
import com.oa.system.mapper.MailMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.MailService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 邮件服务实现类
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailMapper mailMapper;
    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Long sendMail(Mail mail) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        mail.setUserId(userId);
        mail.setFolder("sent");
        mail.setMailId(UUID.randomUUID().toString().replace("-", ""));
        mail.setSenderId(userId);
        mail.setSenderName(user.getRealName());
        mail.setSenderEmail(user.getEmail());
        mail.setSendTime(LocalDateTime.now());

        mailMapper.insert(mail);
        return mail.getId();
    }

    @Override
    public Long saveDraft(Mail mail) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        mail.setUserId(userId);
        mail.setFolder("draft");
        mail.setMailId(UUID.randomUUID().toString().replace("-", ""));
        mail.setSenderId(userId);
        mail.setSenderName(user.getRealName());
        mail.setSenderEmail(user.getEmail());

        mailMapper.insert(mail);
        return mail.getId();
    }

    @Override
    public boolean deleteMail(Long id) {
        Mail mail = mailMapper.selectById(id);
        if (mail != null) {
            mail.setFolder("trash");
            mailMapper.updateById(mail);
        }
        return true;
    }

    @Override
    public Mail getMailById(Long id) {
        Mail mail = mailMapper.selectById(id);
        if (mail != null && mail.getIsRead() == 0) {
            mail.setIsRead(1);
            mailMapper.updateById(mail);
        }
        return mail;
    }

    @Override
    public PageR<Mail> getInboxPage(PageDTO<Mail> pageDTO) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Page<Mail> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Mail> list = mailMapper.selectInboxList(userId);

        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = (int) Math.min(fromIndex + page.getSize(), list.size());

        Page<Mail> result = new Page<>(page.getCurrent(), page.getSize(), list.size());
        if (fromIndex < list.size()) {
            result.setRecords(list.subList(fromIndex, toIndex));
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public PageR<Mail> getSentPage(PageDTO<Mail> pageDTO) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Page<Mail> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Mail> list = mailMapper.selectSentList(userId);

        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = (int) Math.min(fromIndex + page.getSize(), list.size());

        Page<Mail> result = new Page<>(page.getCurrent(), page.getSize(), list.size());
        if (fromIndex < list.size()) {
            result.setRecords(list.subList(fromIndex, toIndex));
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public PageR<Mail> getDraftPage(PageDTO<Mail> pageDTO) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Page<Mail> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Mail> list = mailMapper.selectDraftList(userId);

        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = (int) Math.min(fromIndex + page.getSize(), list.size());

        Page<Mail> result = new Page<>(page.getCurrent(), page.getSize(), list.size());
        if (fromIndex < list.size()) {
            result.setRecords(list.subList(fromIndex, toIndex));
        }

        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean markAsRead(Long id) {
        Mail mail = mailMapper.selectById(id);
        if (mail != null) {
            mail.setIsRead(1);
            mailMapper.updateById(mail);
        }
        return true;
    }

    @Override
    public Long getUnreadCount() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return (long) mailMapper.selectUnreadCount(userId);
    }
}
