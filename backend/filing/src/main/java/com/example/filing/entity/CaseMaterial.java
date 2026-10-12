package com.example.filing.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CaseMaterial {
    private Long id;
    private Long caseId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private Long uploaderId;
    private LocalDateTime createTime;
}