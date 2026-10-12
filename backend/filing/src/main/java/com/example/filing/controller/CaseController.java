package com.example.filing.controller;

import com.example.filing.common.Result;
import com.example.filing.dto.CaseCreateRequest;
import com.example.filing.entity.FilingCase;
import com.example.filing.service.CaseService;
import io.jsonwebtoken.Claims;
import com.example.filing.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private JwtUtil jwtUtil;

    /** 从 token 里取当前用户信息 */
    private Long currentUserId(String authHeader) {
        Claims claims = jwtUtil.parse(authHeader.substring(7));
        return Long.valueOf(claims.getSubject());
    }
    private String currentRole(String authHeader) {
        Claims claims = jwtUtil.parse(authHeader.substring(7));
        return claims.get("role", String.class);
    }

    /** 新建 */
    @PostMapping
    public Result<FilingCase> create(@RequestHeader("Authorization") String auth,
                                     @RequestBody CaseCreateRequest req) {
        FilingCase c = new FilingCase();
        c.setTitle(req.getTitle());
        c.setPlaintiff(req.getPlaintiff());
        c.setDefendant(req.getDefendant());
        c.setAmount(req.getAmount());
        c.setDescription(req.getDescription());
        return Result.ok(caseService.create(c, currentUserId(auth)));
    }

    /** 列表 */
    @GetMapping
    public Result<List<FilingCase>> list(@RequestHeader("Authorization") String auth) {
        return Result.ok(caseService.list(currentUserId(auth), currentRole(auth)));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<FilingCase> detail(@RequestHeader("Authorization") String auth,
                                     @PathVariable Long id) {
        return Result.ok(caseService.getByIdWithAuth(id, currentUserId(auth), currentRole(auth)));
    }

    /** 更新 */
    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader("Authorization") String auth,
                               @PathVariable Long id,
                               @RequestBody CaseCreateRequest req) {
        FilingCase c = new FilingCase();
        c.setTitle(req.getTitle());
        c.setPlaintiff(req.getPlaintiff());
        c.setDefendant(req.getDefendant());
        c.setAmount(req.getAmount());
        c.setDescription(req.getDescription());
        caseService.update(id, c, currentUserId(auth), currentRole(auth));
        return Result.ok();
    }
}