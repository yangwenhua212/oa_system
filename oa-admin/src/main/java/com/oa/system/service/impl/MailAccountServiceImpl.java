package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.MailAccount;
import com.oa.system.mapper.MailAccountMapper;
import com.oa.system.service.MailAccountService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 邮件账号服务实现
 */
@Service
@RequiredArgsConstructor
public class MailAccountServiceImpl implements MailAccountService {

    private final MailAccountMapper mailAccountMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public PageR<MailAccount> getAccountPage(PageDTO<MailAccount> pageDTO) {
        Page<MailAccount> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        MailAccount params = pageDTO.getParams();
        String accountName = params != null ? params.getAccountName() : null;
        LambdaQueryWrapper<MailAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(accountName != null, MailAccount::getAccountName, accountName)
                .orderByDesc(MailAccount::getCreateTime);
        Page<MailAccount> result = mailAccountMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public MailAccount getAccountById(Long id) {
        return mailAccountMapper.selectById(id);
    }

    @Override
    public Long addAccount(MailAccount account) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        account.setUserId(userId);
        account.setCreateTime(LocalDateTime.now());
        mailAccountMapper.insert(account);
        return account.getId();
    }

    @Override
    public boolean updateAccount(MailAccount account) {
        account.setUpdateTime(LocalDateTime.now());
        mailAccountMapper.updateById(account);
        return true;
    }

    @Override
    public boolean deleteAccount(Long id) {
        mailAccountMapper.deleteById(id);
        return true;
    }
}
