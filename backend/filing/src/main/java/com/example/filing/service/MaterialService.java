package com.example.filing.service;

import com.example.filing.entity.CaseMaterial;
import com.example.filing.entity.FilingCase;
import com.example.filing.mapper.CaseMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MaterialService {

    @Autowired
    private CaseMaterialMapper materialMapper;

    @Autowired
    private CaseService caseService;

    @Value("${filing.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of("PDF", "JPEG", "PNG", "JPG");
    private static final long MAX_SIZE = 20 * 1024 * 1024L; // 20MB

    /** 上传材料 */
    public CaseMaterial upload(Long caseId, MultipartFile file, Long currentUserId, String role) throws IOException {
        // 1. 归属校验（只能给自己案件传）
        FilingCase c = caseService.getByIdWithAuth(caseId, currentUserId, role);

        // 2. 案件已提交不能传
        if ("SUBMITTED".equals(c.getStatus())) {
            throw new RuntimeException("已提交的案件不能上传材料");
        }

        // 3. 文件非空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 4. 大小校验
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("文件超过20MB");
        }

        // 5. 类型校验（从文件名后缀判断）
        String originalName = file.getOriginalFilename();
        String ext = extractExt(originalName).toUpperCase();
        if (!ALLOWED_TYPES.contains(ext)) {
            throw new RuntimeException("只允许 PDF/JPEG/PNG");
        }

        // 6. 存磁盘
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String storedName = UUID.randomUUID() + "." + ext.toLowerCase();
        File dest = new File(dir, storedName);
        file.transferTo(dest);

        // 7. 记数据库
        CaseMaterial m = new CaseMaterial();
        m.setCaseId(caseId);
        m.setFileName(originalName);
        m.setFilePath(dest.getAbsolutePath());
        m.setFileType(ext);
        m.setFileSize(file.getSize());
        m.setUploaderId(currentUserId);
        materialMapper.insert(m);

        return m;
    }

    /** 查案件的材料列表 */
    public List<CaseMaterial> listByCase(Long caseId, Long currentUserId, String role) {
        caseService.getByIdWithAuth(caseId, currentUserId, role); // 归属校验
        return materialMapper.findByCaseId(caseId);
    }

    /** 删除材料 */
    public void delete(Long materialId, Long currentUserId, String role) {
        CaseMaterial m = materialMapper.findById(materialId);
        if (m == null) throw new RuntimeException("材料不存在");

        // 归属校验
        FilingCase c = caseService.getByIdWithAuth(m.getCaseId(), currentUserId, role);
        if ("SUBMITTED".equals(c.getStatus())) {
            throw new RuntimeException("已提交的案件不能删除材料");
        }

        // 删磁盘文件
        File f = new File(m.getFilePath());
        if (f.exists()) f.delete();

        // 删数据库
        materialMapper.deleteById(materialId);
    }

    private String extractExt(String name) {
        if (name == null || !name.contains(".")) return "";
        return name.substring(name.lastIndexOf('.') + 1);
    }
}