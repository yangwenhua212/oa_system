package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Leave;

import java.util.List;

/**
 * 请假服务接口
 */
public interface LeaveService {

    /**
     * 提交请假申请
     */
    Long submitLeave(Leave leave);

    /**
     * 更新请假申请
     */
    boolean updateLeave(Leave leave);

    /**
     * 取消请假申请
     */
    boolean cancelLeave(Long id);

    /**
     * 获取请假详情
     */
    Leave getLeaveById(Long id);

    /**
     * 分页查询请假列表（管理端）
     */
    PageR<Leave> getLeavePage(PageDTO<Leave> pageDTO);

    /**
     * 获取我的请假列表
     */
    List<Leave> getMyLeaveList();
}
