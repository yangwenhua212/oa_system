package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询所有部门树
     */
    List<Dept> selectDeptTree();

    /**
     * 查询子部门ID列表
     */
    List<Long> selectChildrenDeptIds(@Param("parentId") Long parentId);

    /**
     * 查询部门列表
     */
    List<Dept> selectDeptList(Dept dept);
}
