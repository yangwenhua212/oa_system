package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.MailAccount;

/**
 * 邮件账号服务接口
 */
public interface MailAccountService {

    PageR<MailAccount> getAccountPage(PageDTO<MailAccount> pageDTO);

    MailAccount getAccountById(Long id);

    Long addAccount(MailAccount account);

    boolean updateAccount(MailAccount account);

    boolean deleteAccount(Long id);
}
