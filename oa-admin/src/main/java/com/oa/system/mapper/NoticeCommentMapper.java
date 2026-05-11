package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.NoticeComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告评论Mapper接口
 */
@Mapper
public interface NoticeCommentMapper extends BaseMapper<NoticeComment> {

    /**
     * 查询公告评论列表
     */
    List<NoticeComment> selectByNoticeId(@Param("noticeId") Long noticeId);
}
