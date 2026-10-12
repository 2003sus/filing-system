package com.example.filing.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FilingTask {
    private Long id;
    private Long caseId;
    private String status;        // DRAFT/QUEUED/FILLING/WAIT_REVIEW/SUBMITTED/FAILED
    private String snapshot;      // JSON快照
    private String failReason;
    private String applyNo;       // 申请编号
    private Long reviewerId;
    private LocalDateTime submitTime;
    private Integer version;      // 乐观锁
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}