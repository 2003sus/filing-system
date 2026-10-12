package com.example.filing.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String role;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}