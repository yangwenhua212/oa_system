package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.AddressBook;
import com.oa.system.entity.AddressGroup;
import com.oa.system.entity.AddressShare;
import com.oa.system.entity.Dept;
import com.oa.system.entity.Position;
import com.oa.system.entity.User;
import com.oa.system.mapper.AddressBookMapper;
import com.oa.system.mapper.AddressGroupMapper;
import com.oa.system.mapper.AddressShareMapper;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.PositionMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通讯录控制器
 */
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressBookMapper addressBookMapper;
    private final AddressGroupMapper addressGroupMapper;
    private final AddressShareMapper addressShareMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final PositionMapper positionMapper;
    private final JwtTokenUtil jwtTokenUtil;

    // ==================== 联系人 CRUD ====================

    /**
     * 获取内部通讯录（公司员工列表）
     */
    @GetMapping("/internal")
    public R internal() {
        List<User> users = userMapper.selectList(null);
        List<Map<String, Object>> list = users.stream().map(u -> {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", u.getId());
            item.put("username", u.getUsername());
            item.put("realName", u.getRealName());
            item.put("email", u.getEmail());
            item.put("phone", u.getPhone());
            item.put("deptId", u.getDeptId());
            // 查询部门名称
            if (u.getDeptId() != null) {
                Dept dept = deptMapper.selectById(u.getDeptId());
                item.put("deptName", dept != null ? dept.getDeptName() : "");
            } else {
                item.put("deptName", "");
            }
            // 查询职位名称
            if (u.getPositionId() != null) {
                Position position = positionMapper.selectById(u.getPositionId());
                item.put("positionName", position != null ? position.getPositionName() : "");
            } else {
                item.put("positionName", "");
            }
            return item;
        }).collect(Collectors.toList());
        return R.ok(list);
    }

    /**
     * 获取外部通讯录（用户自定义联系人）
     */
    @GetMapping("/external")
    public R external() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<AddressBook> list = addressBookMapper.selectMyAddressBookList(userId);
        return R.ok(list);
    }

    // ==================== 分类管理 ====================

    /**
     * 获取分组列表
     */
    @GetMapping("/categories")
    public R categories() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<AddressGroup> groups = addressBookMapper.selectAddressGroupList(userId);
        return R.ok(groups);
    }

    /**
     * 新增分类
     */
    @PostMapping("/categories")
    public R addCategory(@RequestBody Map<String, String> params) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        AddressGroup group = new AddressGroup();
        group.setUserId(userId);
        group.setGroupName(params.get("name"));
        addressGroupMapper.insert(group);
        return R.ok(group.getId());
    }

    /**
     * 修改分类
     */
    @PutMapping("/categories/{id}")
    public R updateCategory(@PathVariable Long id, @RequestBody Map<String, String> params) {
        AddressGroup group = addressGroupMapper.selectById(id);
        if (group != null) {
            group.setGroupName(params.get("name"));
            addressGroupMapper.updateById(group);
        }
        return R.ok();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{id}")
    public R deleteCategory(@PathVariable Long id) {
        addressGroupMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 获取通讯录列表
     */
    @GetMapping("/list")
    public R list(AddressBook addressBook) {
        List<AddressBook> list = addressBookMapper.selectAddressBookList(addressBook);
        return R.ok(list);
    }

    /**
     * 新增联系人
     */
    @PostMapping("/contacts")
    public R add(@RequestBody AddressBook addressBook) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        addressBook.setUserId(userId);
        addressBookMapper.insert(addressBook);
        return R.ok(addressBook.getId());
    }

    /**
     * 修改联系人
     */
    @PutMapping("/contacts")
    public R update(@RequestBody AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
        return R.ok();
    }

    /**
     * 删除联系人
     */
    @DeleteMapping("/contacts/{id}")
    public R deleteContact(@PathVariable Long id) {
        addressBookMapper.deleteById(id);
        return R.ok();
    }

    // ==================== 分享功能 ====================

    /**
     * 获取我分享的联系人
     */
    @GetMapping("/shared/by-me")
    public R sharedByMe() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<AddressShare> list = addressShareMapper.selectBySharedBy(userId);
        return R.ok(list);
    }

    /**
     * 获取分享给我的联系人
     */
    @GetMapping("/shared/with-me")
    public R sharedWithMe() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<AddressShare> list = addressShareMapper.selectBySharedWith(userId);
        return R.ok(list);
    }

    /**
     * 分享联系人给其他用户
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/shared")
    public R share(@RequestBody Map<String, Object> params) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Long contactId;
        try {
            contactId = Long.valueOf(params.get("contactId").toString());
        } catch (NumberFormatException e) {
            return R.error(400, "无效的联系人ID");
        }
        List<Integer> userIds = (List<Integer>) params.get("userIds");

        for (Integer toUserId : userIds) {
            AddressShare share = new AddressShare();
            share.setContactId(contactId);
            share.setSharedBy(userId);
            share.setSharedWith(toUserId.longValue());
            addressShareMapper.insert(share);
        }
        return R.ok();
    }

    /**
     * 取消分享
     */
    @DeleteMapping("/shared/{id}")
    public R cancelShare(@PathVariable Long id) {
        addressShareMapper.deleteById(id);
        return R.ok();
    }
}
