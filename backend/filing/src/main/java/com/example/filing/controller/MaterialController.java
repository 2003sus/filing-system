package com.example.filing.controller;

import com.example.filing.common.Result;
import com.example.filing.entity.CaseMaterial;
import com.example.filing.security.JwtUtil;
import com.example.filing.service.MaterialService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private JwtUtil jwtUtil;

    private Claims claims(String auth) {
        return jwtUtil.parse(auth.substring(7));
    }
    private Long uid(String auth) {
        return Long.valueOf(claims(auth).getSubject());
    }
    private String role(String auth) {
        return claims(auth).get("role", String.class);
    }

    /** 上传材料 */
    @PostMapping
    public Result<CaseMaterial> upload(@RequestHeader("Authorization") String auth,
                                       @PathVariable Long caseId,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        return Result.ok(materialService.upload(caseId, file, uid(auth), role(auth)));
    }

    /** 查材料列表 */
    @GetMapping
    public Result<List<CaseMaterial>> list(@RequestHeader("Authorization") String auth,
                                           @PathVariable Long caseId) {
        return Result.ok(materialService.listByCase(caseId, uid(auth), role(auth)));
    }

    /** 删除材料 */
    @DeleteMapping("/{materialId}")
    public Result<Void> delete(@RequestHeader("Authorization") String auth,
                               @PathVariable Long caseId,
                               @PathVariable Long materialId) {
        materialService.delete(materialId, uid(auth), role(auth));
        return Result.ok();
    }
}