package com.example.filing.controller;

import com.example.filing.common.Result;
import com.example.filing.entity.FilingTask;
import com.example.filing.security.JwtUtil;
import com.example.filing.service.TaskService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JwtUtil jwtUtil;

    private Claims claims(String auth) { return jwtUtil.parse(auth.substring(7)); }
    private Long uid(String auth) { return Long.valueOf(claims(auth).getSubject()); }
    private String role(String auth) { return claims(auth).get("role", String.class); }

    /** 创建任务 */
    @PostMapping
    public Result<FilingTask> create(@RequestHeader("Authorization") String auth,
                                     @RequestBody Map<String, Long> body) {
        Long caseId = body.get("caseId");
        if (caseId == null) throw new RuntimeException("caseId 不能为空");
        return Result.ok(taskService.create(caseId, uid(auth), role(auth)));
    }

    /** 任务列表 */
    @GetMapping
    public Result<List<FilingTask>> list(@RequestHeader("Authorization") String auth) {
        return Result.ok(taskService.list(uid(auth), role(auth)));
    }

    /** 任务详情 */
    @GetMapping("/{id}")
    public Result<FilingTask> detail(@RequestHeader("Authorization") String auth,
                                     @PathVariable Long id) {
        return Result.ok(taskService.getByIdWithAuth(id, uid(auth), role(auth)));
    }

    /** 复核：退回（仅REVIEWER/ADMIN） */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public Result<Void> reject(@RequestHeader("Authorization") String auth,
                               @PathVariable Long id,
                               @RequestBody Map<String, String> body) {
        taskService.reject(id, body.get("reason"), uid(auth), role(auth));
        return Result.ok();
    }

    /** 复核：确认提交（仅REVIEWER/ADMIN） */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public Result<Void> submit(@RequestHeader("Authorization") String auth,
                               @PathVariable Long id) {
        taskService.confirmSubmit(id, uid(auth), role(auth));
        return Result.ok();
    }
}