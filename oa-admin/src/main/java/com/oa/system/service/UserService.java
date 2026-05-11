package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 获取当前登录用户信息
     */
    User getCurrentUser();

    /**
     * 获取当前用户菜单
     */
    List getCurrentUserMenus();

    /**
     * 分页查询用户列表
     */
    PageR<User> getUserPage(PageDTO<User> pageDTO);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 新增用户
     */
    Long addUser(User user);

    /**
     * 修改用户
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 重置密码
     */
    boolean resetPassword(Long id, String password);

    /**
     * 修改密码
     */
    boolean changePassword(String oldPassword, String newPassword);

    /**
     * 获取所有用户（用于下拉选择）
     */
    List<User> getAllUsers();

    /**
     * 查询部门下所有用户
     */
    List<User> getUsersByDept(Long deptId);
}
