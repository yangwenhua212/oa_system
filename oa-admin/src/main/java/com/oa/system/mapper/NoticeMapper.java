package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告Mapper接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 查询通知列表
     */
    List<Notice> selectNoticeList(Notice notice);

    /**
     * 查询我的通知
     */
    List<Notice> selectMyNoticeList(@Param("userId") Long userId);

    /**
     * 更新浏览次数
     */
    void updateViewCount(@Param("id") Long id);

    /**
     * 查询未读通知数量
     */
    Long countUnread(@Param("userId") Long userId);

    /**
     * 插入已读记录
     */
    void insertRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
}
