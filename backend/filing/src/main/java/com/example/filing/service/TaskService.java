package com.example.filing.service;

import com.example.filing.entity.CaseMaterial;
import com.example.filing.entity.FilingCase;
import com.example.filing.entity.FilingTask;
import com.example.filing.mapper.CaseMaterialMapper;
import com.example.filing.mapper.FilingTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private FilingTaskMapper taskMapper;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseMaterialMapper materialMapper;

    private final ObjectMapper json = new ObjectMapper();

    /** 创建任务 */
    @Transactional
    public FilingTask create(Long caseId, Long currentUserId, String role) {
        // 1. 归属校验
        FilingCase c = caseService.getByIdWithAuth(caseId, currentUserId, role);

        // 2. 已提交案件不能再建任务
        if ("SUBMITTED".equals(c.getStatus())) {
            throw new RuntimeException("已提交的案件不能再创建立案任务");
        }

        // 3. 必填字段校验
        if (c.getTitle() == null || c.getTitle().isBlank()
                || c.getPlaintiff() == null || c.getPlaintiff().isBlank()
                || c.getDefendant() == null || c.getDefendant().isBlank()) {
            throw new RuntimeException("案件必填字段不全（案由/原告/被告）");
        }

        // 4. 至少一份材料
        List<CaseMaterial> materials = materialMapper.findByCaseId(caseId);
        if (materials.isEmpty()) {
            throw new RuntimeException("至少上传一份材料才能创建立案任务");
        }

        // 5. 同一案件只能有一个未结束任务
        FilingTask unfinished = taskMapper.findUnfinishedByCaseId(caseId);
        if (unfinished != null) {
            throw new RuntimeException("该案件已有未结束的任务（id=" + unfinished.getId() + "）");
        }

        // 6. 生成快照
        Map<String, Object> snap = new HashMap<>();
        snap.put("caseId", c.getId());
        snap.put("caseNo", c.getCaseNo());
        snap.put("title", c.getTitle());
        snap.put("plaintiff", c.getPlaintiff());
        snap.put("defendant", c.getDefendant());
        snap.put("amount", c.getAmount());
        snap.put("description", c.getDescription());
        snap.put("materials", materials.stream().map(m -> {
            Map<String, Object> mm = new HashMap<>();
            mm.put("id", m.getId());
            mm.put("fileName", m.getFileName());
            mm.put("filePath", m.getFilePath());
            mm.put("fileType", m.getFileType());
            return mm;
        }).toList());

        String snapshotJson;
        try {
            snapshotJson = json.writeValueAsString(snap);
        } catch (Exception e) {
            throw new RuntimeException("生成快照失败: " + e.getMessage());
        }

        // 7. 入库，状态 QUEUED
        FilingTask t = new FilingTask();
        t.setCaseId(caseId);
        t.setStatus("QUEUED");
        t.setSnapshot(snapshotJson);
        taskMapper.insert(t);

        return t;
    }

    /** 查任务列表（带归属过滤） */
    public List<FilingTask> list(Long currentUserId, String role) {
        // 管理员和复核员：看全部
        if ("ADMIN".equals(role) || "REVIEWER".equals(role)) {
            return taskMapper.findAll();
        }
        // 员工：只看自己案件的
        List<FilingCase> myCases = caseService.list(currentUserId, role);
        return myCases.stream()
                .flatMap(c -> taskMapper.findByCaseId(c.getId()).stream())
                .toList();
    }

    /** 查单个任务（带归属校验） */
    public FilingTask getByIdWithAuth(Long id, Long currentUserId, String role) {
        FilingTask t = taskMapper.findById(id);
        if (t == null) throw new RuntimeException("任务不存在");

        // 管理员和复核员：能看所有任务
        if ("ADMIN".equals(role) || "REVIEWER".equals(role)) {
            return t;
        }

        // 员工：只能看自己案件的任务
        caseService.getByIdWithAuth(t.getCaseId(), currentUserId, role);
        return t;
    }

    /** 复核：退回 */
    @Transactional
    public void reject(Long taskId, String reason, Long reviewerId, String role) {
        FilingTask t = getByIdWithAuth(taskId, reviewerId, role);
        if (!"WAIT_REVIEW".equals(t.getStatus())) {
            throw new RuntimeException("只有 WAIT_REVIEW 状态才能退回");
        }
        t.setStatus("QUEUED");
        t.setFailReason("复核退回：" + reason);
        t.setReviewerId(reviewerId);
        taskMapper.update(t);
    }

    /** 复核：确认提交 */
    @Transactional
    public void confirmSubmit(Long taskId, Long reviewerId, String role) {
        FilingTask t = getByIdWithAuth(taskId, reviewerId, role);
        if (!"WAIT_REVIEW".equals(t.getStatus())) {
            throw new RuntimeException("只有 WAIT_REVIEW 状态才能提交");
        }
        if (t.getApplyNo() != null) {
            throw new RuntimeException("该任务已提交过，不能重复提交");
        }

        String applyNo = "MOCK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", t.getId());

        t.setStatus("SUBMITTED");
        t.setApplyNo(applyNo);
        t.setReviewerId(reviewerId);
        t.setSubmitTime(LocalDateTime.now());
        taskMapper.update(t);

        caseService.updateStatus(t.getCaseId(), "SUBMITTED");
    }
}