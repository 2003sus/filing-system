package com.example.filing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CaseCreateRequest {
    private String title;
    private String plaintiff;
    private String defendant;
    private BigDecimal amount;
    private String description;
}