package com.example.filing.mapper;

import com.example.filing.entity.FilingTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FilingTaskMapper {

    @Insert("INSERT INTO filing_task(case_id, status, snapshot, version) " +
            "VALUES(#{caseId}, #{status}, #{snapshot}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FilingTask t);

    @Select("SELECT * FROM filing_task WHERE id = #{id}")
    FilingTask findById(@Param("id") Long id);

    @Select("SELECT * FROM filing_task WHERE case_id = #{caseId} ORDER BY id DESC")
    List<FilingTask> findByCaseId(@Param("caseId") Long caseId);

    @Select("SELECT * FROM filing_task ORDER BY id DESC")
    List<FilingTask> findAll();

    @Select("SELECT * FROM filing_task WHERE case_id = #{caseId} " +
            "AND status NOT IN ('SUBMITTED','FAILED') LIMIT 1")
    FilingTask findUnfinishedByCaseId(@Param("caseId") Long caseId);

    /** 乐观锁更新：只有version匹配才更新 */
    @Update("UPDATE filing_task SET status=#{status}, fail_reason=#{failReason}, " +
            "apply_no=#{applyNo}, reviewer_id=#{reviewerId}, submit_time=#{submitTime}, " +
            "version=version+1 WHERE id=#{id} AND version=#{version}")
    int updateWithVersion(FilingTask t);

    /** 简单状态更新（不带乐观锁，用于复核） */
    @Update("UPDATE filing_task SET status=#{status}, fail_reason=#{failReason}, " +
            "apply_no=#{applyNo}, reviewer_id=#{reviewerId}, submit_time=#{submitTime} " +
            "WHERE id=#{id}")
    int update(FilingTask t);

    @Select("SELECT * FROM filing_task WHERE status='QUEUED' ORDER BY id LIMIT 1")
    FilingTask findNextQueued();

    @Update("UPDATE filing_task SET status='FILLING', version=version+1 " +
            "WHERE id=#{id} AND version=#{version} AND status='QUEUED'")
    int markFilling(@Param("id") Long id, @Param("version") Integer version);

    @Update("UPDATE filing_task SET status='WAIT_REVIEW', apply_no=#{applyNo} WHERE id=#{id}")
    int markWaitReview(@Param("id") Long id, @Param("applyNo") String applyNo);

    @Update("UPDATE filing_task SET status='FAILED', fail_reason=#{reason} WHERE id=#{id}")
    int markFailed(@Param("id") Long id, @Param("reason") String reason);
}