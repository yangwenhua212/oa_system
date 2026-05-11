package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.ChatGroup;
import com.oa.system.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天Mapper接口
 */
@Mapper
public interface ChatGroupMapper extends BaseMapper<ChatGroup> {

    /**
     * 查询我的群聊列表
     */
    List<ChatGroup> selectMyGroupList(@Param("userId") Long userId);

    /**
     * 查询群消息
     */
    List<ChatMessage> selectGroupMessageList(@Param("groupId") Long groupId, @Param("limit") Integer limit);
}
