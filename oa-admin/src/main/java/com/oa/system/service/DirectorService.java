package com.oa.system.service;

import com.oa.system.entity.Director;

import java.util.List;

/**
 * 通讯录服务接口
 */
public interface DirectorService {

    /**
     * 获取通讯录列表
     */
    List<Director> getDirectorList();

    /**
     * 根据ID查询
     */
    Director getDirectorById(Long id);

    /**
     * 新增联系人
     */
    Long addDirector(Director director);

    /**
     * 修改联系人
     */
    boolean updateDirector(Director director);

    /**
     * 删除联系人
     */
    boolean deleteDirector(Long id);

    /**
     * 根据姓名搜索
     */
    List<Director> searchByName(String name);
}
