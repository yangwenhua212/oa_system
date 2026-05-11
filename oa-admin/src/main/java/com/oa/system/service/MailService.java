package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Mail;

import java.util.List;

/**
 * 邮件服务接口
 */
public interface MailService {

    /**
     * 发送邮件
     */
    Long sendMail(Mail mail);

    /**
     * 保存草稿
     */
    Long saveDraft(Mail mail);

    /**
     * 删除邮件
     */
    boolean deleteMail(Long id);

    /**
     * 获取邮件详情
     */
    Mail getMailById(Long id);

    /**
     * 获取收件箱邮件
     */
    PageR<Mail> getInboxPage(PageDTO<Mail> pageDTO);

    /**
     * 获取发件箱邮件
     */
    PageR<Mail> getSentPage(PageDTO<Mail> pageDTO);

    /**
     * 获取草稿箱邮件
     */
    PageR<Mail> getDraftPage(PageDTO<Mail> pageDTO);

    /**
     * 标记邮件已读
     */
    boolean markAsRead(Long id);

    /**
     * 获取未读邮件数量
     */
    Long getUnreadCount();
}
