package com.example.filing.mapper;

import com.example.filing.entity.FilingCase;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FilingCaseMapper {

    @Insert("INSERT INTO filing_case(case_no, title, plaintiff, defendant, amount, description, status, creator_id) " +
            "VALUES(#{caseNo}, #{title}, #{plaintiff}, #{defendant}, #{amount}, #{description}, #{status}, #{creatorId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FilingCase c);

    @Select("SELECT * FROM filing_case WHERE id = #{id}")
    FilingCase findById(@Param("id") Long id);

    @Select("SELECT * FROM filing_case WHERE creator_id = #{creatorId} ORDER BY id DESC")
    List<FilingCase> findByCreator(@Param("creatorId") Long creatorId);

    @Select("SELECT * FROM filing_case ORDER BY id DESC")
    List<FilingCase> findAll();

    @Update("UPDATE filing_case SET title=#{title}, plaintiff=#{plaintiff}, defendant=#{defendant}, " +
            "amount=#{amount}, description=#{description} WHERE id=#{id}")
    int update(FilingCase c);

    @Update("UPDATE filing_case SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}