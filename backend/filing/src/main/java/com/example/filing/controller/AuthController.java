package com.example.filing.controller;

import com.example.filing.common.Result;
import com.example.filing.dto.LoginRequest;
import com.example.filing.entity.SysUser;
import com.example.filing.security.JwtUtil;
import com.example.filing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        System.out.println("=== 收到登录请求 ===");
        System.out.println("username: " + req.getUsername());
        System.out.println("password: " + req.getPassword());

        SysUser user = userService.findByUsername(req.getUsername());
        System.out.println("查到用户: " + user);

        if (user == null) {
            return Result.fail(401, "用户名或密码错误");
        }

        System.out.println("数据库里的哈希: " + user.getPasswordHash());
        boolean match = passwordEncoder.matches(req.getPassword(), user.getPasswordHash());
        System.out.println("密码匹配结果: " + match);

        if (user.getEnabled() == null || !user.getEnabled()) {
            return Result.fail(403, "账号已禁用");
        }

        if (!match) {
            return Result.fail(401, "用户名或密码错误");
        }

        String token = jwtUtil.generate(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());

        return Result.ok(data);
    }
    @GetMapping("/me")
    public Result<Map<String, Object>> me(org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            return Result.fail(401, "未登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", auth.getPrincipal());
        data.put("username", auth.getDetails());
        data.put("authorities", auth.getAuthorities());
        return Result.ok(data);
    }
}