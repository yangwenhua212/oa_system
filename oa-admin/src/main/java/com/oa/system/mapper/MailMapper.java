package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Mail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 邮件Mapper接口
 */
@Mapper
public interface MailMapper extends BaseMapper<Mail> {

    /**
     * 查询邮件列表
     */
    List<Mail> selectMailList(Mail mail);

    /**
     * 查询收件箱邮件
     */
    List<Mail> selectInboxList(@Param("userId") Long userId);

    /**
     * 查询发件箱邮件
     */
    List<Mail> selectSentList(@Param("userId") Long userId);

    /**
     * 查询草稿箱邮件
     */
    List<Mail> selectDraftList(@Param("userId") Long userId);

    /**
     * 查询未读邮件数量
     */
    Integer selectUnreadCount(@Param("userId") Long userId);
}
