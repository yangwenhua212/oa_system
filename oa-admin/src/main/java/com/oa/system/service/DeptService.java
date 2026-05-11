package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DeptService {

    /**
     * 获取部门树
     */
    List<Dept> getDeptTree();

    /**
     * 分页查询部门列表
     */
    PageR<Dept> getDeptPage(PageDTO<Dept> pageDTO);

    /**
     * 根据ID查询部门
     */
    Dept getDeptById(Long id);

    /**
     * 新增部门
     */
    Long addDept(Dept dept);

    /**
     * 修改部门
     */
    boolean updateDept(Dept dept);

    /**
     * 删除部门
     */
    boolean deleteDept(Long id);

    /**
     * 获取所有部门（用于下拉选择）
     */
    List<Dept> getAllDepts();
}
