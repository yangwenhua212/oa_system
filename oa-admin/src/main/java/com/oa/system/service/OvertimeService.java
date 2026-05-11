package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Overtime;

import java.util.List;

/**
 * 加班服务接口
 */
public interface OvertimeService {

    /**
     * 提交加班申请
     */
    Long submitOvertime(Overtime overtime);

    /**
     * 更新加班申请
     */
    boolean updateOvertime(Overtime overtime);

    /**
     * 取消加班申请
     */
    boolean cancelOvertime(Long id);

    /**
     * 获取加班详情
     */
    Overtime getOvertimeById(Long id);

    /**
     * 分页查询加班列表（管理端）
     */
    PageR<Overtime> getOvertimePage(PageDTO<Overtime> pageDTO);

    /**
     * 获取我的加班列表
     */
    List<Overtime> getMyOvertimeList();
}
