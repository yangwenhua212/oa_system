package com.oa.system.service.impl;

import com.oa.system.entity.ApprovalRecord;
import com.oa.system.entity.ProcessInstance;
import com.oa.system.entity.User;
import com.oa.system.mapper.ApprovalRecordMapper;
import com.oa.system.mapper.ProcessInstanceMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.security.JwtTokenUtil;
import com.oa.system.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessInstanceMapper processInstanceMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public Long submitProcess(Map<String, Object> formData) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        ProcessInstance instance = new ProcessInstance();
        instance.setTitle((String) formData.getOrDefault("title", ""));
        instance.setProcessDefinitionId(1L);
        instance.setProcessKey((String) formData.getOrDefault("businessType", "expense"));
        instance.setBusinessKey((String) formData.getOrDefault("businessType", ""));
        instance.setBusinessType((String) formData.getOrDefault("businessType", "expense"));
        instance.setProcessName((String) formData.getOrDefault("processName", ""));
        instance.setApplicantId(userId);
        instance.setApplicantName(user.getRealName());
        instance.setCurrentNodeName("待审批");
        instance.setStatus("pending");
        instance.setStartTime(LocalDateTime.now());
        instance.setDelFlag(0);

        // 如果有审核人，设置为当前审核人
        Object auditor = formData.get("auditor");
        if (auditor != null && !auditor.toString().trim().isEmpty()) {
            try {
                instance.setCurrentApproverId(Long.valueOf(auditor.toString().trim()));
            } catch (NumberFormatException e) {
                // ignore - 审核人字段可能为姓名而非ID
            }
        }

        // 保存表单数据JSON
        Object formDataJson = formData.get("formData");
        if (formDataJson != null) {
            instance.setFormData(formDataJson.toString());
        }

        processInstanceMapper.insert(instance);
        return instance.getId();
    }

    @Override
    @Transactional
    public boolean approve(Long processId, String action, String comment) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        ProcessInstance instance = processInstanceMapper.selectById(processId);
        if (instance == null) return false;

        // 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setProcessInstanceId(processId);
        record.setApproverId(userId);
        record.setApproverName(user.getRealName());
        record.setAction(action);
        record.setComment(comment);
        record.setEndTime(LocalDateTime.now());
        approvalRecordMapper.insert(record);

        // 更新流程状态
        if ("approve".equals(action)) {
            instance.setStatus("approved");
            instance.setCurrentNodeName("已通过");
        } else if ("reject".equals(action)) {
            instance.setStatus("rejected");
            instance.setCurrentNodeName("已拒绝");
        }
        instance.setEndTime(LocalDateTime.now());
        processInstanceMapper.updateById(instance);
        return true;
    }

    @Override
    public List<ProcessInstance> getMyProcessList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return processInstanceMapper.selectMyProcessList(userId);
    }

    @Override
    public List<ProcessInstance> getPendingProcessList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return processInstanceMapper.selectPendingProcessList(userId);
    }

    @Override
    public ProcessInstance getProcessById(Long id) {
        return processInstanceMapper.selectById(id);
    }

    @Override
    public List<ApprovalRecord> getApprovalRecords(Long processInstanceId) {
        return approvalRecordMapper.selectByProcessInstanceId(processInstanceId);
    }
}
