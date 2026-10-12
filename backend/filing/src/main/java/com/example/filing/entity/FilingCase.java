package com.example.filing.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FilingCase {
    private Long id;
    private String caseNo;
    private String title;
    private String plaintiff;
    private String defendant;
    private BigDecimal amount;
    private String description;
    private String status;       // DRAFT / SUBMITTED
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}