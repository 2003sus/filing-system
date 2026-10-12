package com.example.filing.controller;

import com.example.filing.common.Result;
import com.example.filing.entity.SysUser;
import com.example.filing.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserMapper userMapper;

    // 只有 ADMIN 能访问
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SysUser>> list() {
        List<SysUser> users = userMapper.findAll();
        return Result.ok(users);
    }

    // 只有 ADMIN 能访问
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> disable(@PathVariable Long id) {
        userMapper.disable(id);
        return Result.ok();
    }
}