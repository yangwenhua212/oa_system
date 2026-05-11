package com.oa.system.service;

import com.oa.system.entity.ApprovalRecord;
import com.oa.system.entity.ProcessInstance;

import java.util.List;
import java.util.Map;

public interface ProcessService {

    /** 提交审批 */
    Long submitProcess(Map<String, Object> formData);

    /** 审批操作（通过/拒绝） */
    boolean approve(Long processId, String action, String comment);

    /** 获取我的申请列表 */
    List<ProcessInstance> getMyProcessList();

    /** 获取待我审批列表 */
    List<ProcessInstance> getPendingProcessList();

    /** 获取流程详情 */
    ProcessInstance getProcessById(Long id);

    /** 获取审批记录 */
    List<ApprovalRecord> getApprovalRecords(Long processInstanceId);
}
