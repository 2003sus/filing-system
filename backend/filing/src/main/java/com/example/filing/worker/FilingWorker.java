package com.example.filing.worker;

import com.example.filing.entity.FilingTask;
import com.example.filing.mapper.FilingTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FilingWorker {

    @Autowired
    private FilingTaskMapper taskMapper;

    @Autowired
    private CourtPageFiller filler;

    @Value("${filing.worker.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelay = 3000)
    public void pollAndRun() {
        if (!enabled) return;

        FilingTask task = taskMapper.findNextQueued();
        if (task == null) return;

        System.out.println("=== Worker 领取任务 id=" + task.getId() + " ===");

        int updated = taskMapper.markFilling(task.getId(), task.getVersion());
        if (updated == 0) {
            System.out.println("任务已被其他 worker 领取，跳过");
            return;
        }

        try {
            String applyNo = filler.fillAndSubmit(task.getSnapshot());
            System.out.println("=== 填表成功，申请编号: " + applyNo + " ===");
            taskMapper.markWaitReview(task.getId(), applyNo);
        } catch (Exception e) {
            System.out.println("=== 填表失败: " + e.getMessage() + " ===");
            e.printStackTrace();
            taskMapper.markFailed(task.getId(), e.getMessage());
        }
    }
}