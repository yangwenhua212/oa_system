package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND del_flag = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 查询用户列表（包含部门、职位信息）
     */
    List<User> selectUserList(User user);

    /**
     * 根据关键词搜索用户（跨部门、职位）
     */
    List<User> searchUserByKeyword(@Param("keyword") String keyword);

    /**
     * 根据用户ID查询角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单权限
     */
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询菜单列表
     */
    List<Long> selectMenuIdsByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关系
     */
    int batchInsertUserRole(@Param("userId") Long userId, @Param("roleIds") Long[] roleIds);

    /**
     * 删除用户角色关系
     */
    int deleteUserRole(@Param("userId") Long userId);
}
