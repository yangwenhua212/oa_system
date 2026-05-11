package com.oa.system.service;

import com.oa.system.entity.Discuss;

import java.util.List;

/**
 * 讨论区服务接口
 */
public interface DiscussService {

    /**
     * 获取讨论列表
     */
    List<Discuss> getDiscussList();

    /**
     * 根据ID查询
     */
    Discuss getDiscussById(Long id);

    /**
     * 新增讨论
     */
    Long addDiscuss(Discuss discuss);

    /**
     * 修改讨论
     */
    boolean updateDiscuss(Discuss discuss);

    /**
     * 删除讨论
     */
    boolean deleteDiscuss(Long id);

    /**
     * 增加浏览次数
     */
    boolean incrementVisit(Long id);
}
