package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

    List<ApprovalRecord> selectByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);
}
