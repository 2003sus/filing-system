package com.example.filing.service;

import com.example.filing.entity.FilingCase;
import com.example.filing.mapper.FilingCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CaseService {

    @Autowired
    private FilingCaseMapper caseMapper;

    /** 新建案件 */
    public FilingCase create(FilingCase c, Long creatorId) {
        c.setCaseNo(generateCaseNo());
        c.setStatus("DRAFT");
        c.setCreatorId(creatorId);
        caseMapper.insert(c);
        return c;
    }

    /** 查单个（带归属校验） */
    public FilingCase getByIdWithAuth(Long id, Long currentUserId, String role) {
        FilingCase c = caseMapper.findById(id);
        if (c == null) {
            throw new RuntimeException("案件不存在");
        }
        // 管理员可看全部，其他只能看自己的
        if (!"ADMIN".equals(role) && !c.getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("无权访问该案件");
        }
        return c;
    }

    /** 列表（带归属过滤） */
    public List<FilingCase> list(Long currentUserId, String role) {
        if ("ADMIN".equals(role)) {
            return caseMapper.findAll();
        }
        return caseMapper.findByCreator(currentUserId);
    }

    /** 更新（带归属和状态校验） */
    public void update(Long id, FilingCase req, Long currentUserId, String role) {
        FilingCase c = getByIdWithAuth(id, currentUserId, role);
        if ("SUBMITTED".equals(c.getStatus())) {
            throw new RuntimeException("已提交的案件不能修改");
        }
        req.setId(id);
        caseMapper.update(req);
    }

    /** 生成案件编号 */
    private String generateCaseNo() {
        return "CASE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + (int)(Math.random() * 9000 + 1000);
    }

    public void updateStatus(Long caseId, String status) {
        caseMapper.updateStatus(caseId, status);
    }
}