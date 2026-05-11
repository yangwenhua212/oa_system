package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.MailAccount;
import com.oa.system.service.MailAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件账号控制器
 */
@RestController
@RequestMapping("/api/mail/account")
@RequiredArgsConstructor
public class MailAccountController {

    private final MailAccountService mailAccountService;

    @GetMapping("/list")
    public R list(PageDTO<MailAccount> pageDTO) {
        PageR<MailAccount> result = mailAccountService.getAccountPage(pageDTO);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        MailAccount account = mailAccountService.getAccountById(id);
        return R.ok(account);
    }

    @PostMapping
    public R add(@RequestBody MailAccount account) {
        Long id = mailAccountService.addAccount(account);
        return R.ok(id);
    }

    @PutMapping
    public R update(@RequestBody MailAccount account) {
        mailAccountService.updateAccount(account);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        mailAccountService.deleteAccount(id);
        return R.ok();
    }
}
