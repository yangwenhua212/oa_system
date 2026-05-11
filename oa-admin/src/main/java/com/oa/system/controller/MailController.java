package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Mail;
import com.oa.system.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件控制器
 */
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    /**
     * 发送邮件
     */
    @PostMapping
    public R send(@RequestBody Mail mail) {
        Long id = mailService.sendMail(mail);
        return R.ok(id);
    }

    /**
     * 保存草稿
     */
    @PostMapping("/draft")
    public R saveDraft(@RequestBody Mail mail) {
        Long id = mailService.saveDraft(mail);
        return R.ok(id);
    }

    /**
     * 删除邮件
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        mailService.deleteMail(id);
        return R.ok();
    }

    /**
     * 获取邮件详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Mail mail = mailService.getMailById(id);
        return R.ok(mail);
    }

    /**
     * 获取收件箱
     */
    @GetMapping("/inbox")
    public R inbox(PageDTO<Mail> pageDTO) {
        PageR<Mail> result = mailService.getInboxPage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取发件箱
     */
    @GetMapping("/sent")
    public R sent(PageDTO<Mail> pageDTO) {
        PageR<Mail> result = mailService.getSentPage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取草稿箱
     */
    @GetMapping("/draft")
    public R draft(PageDTO<Mail> pageDTO) {
        PageR<Mail> result = mailService.getDraftPage(pageDTO);
        return R.ok(result);
    }

    /**
     * 标记邮件已读
     */
    @PutMapping("/read/{id}")
    public R markAsRead(@PathVariable Long id) {
        mailService.markAsRead(id);
        return R.ok();
    }

    /**
     * 获取未读邮件数量
     */
    @GetMapping("/unread")
    public R unreadCount() {
        Long count = mailService.getUnreadCount();
        return R.ok(count);
    }
}
