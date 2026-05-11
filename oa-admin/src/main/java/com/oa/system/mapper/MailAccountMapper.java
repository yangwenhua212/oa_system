package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.MailAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件账号Mapper
 */
@Mapper
public interface MailAccountMapper extends BaseMapper<MailAccount> {
}
