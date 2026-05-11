package com.oa.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("oa_approval_record")
public class ApprovalRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long processInstanceId;
    private Long taskId;
    private String nodeKey;
    private String nodeName;
    private Long approverId;
    private String approverName;
    private String action;
    private String comment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
