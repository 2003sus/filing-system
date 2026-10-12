package com.example.filing.mapper;

import com.example.filing.entity.CaseMaterial;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CaseMaterialMapper {

    @Insert("INSERT INTO case_material(case_id, file_name, file_path, file_type, file_size, uploader_id) " +
            "VALUES(#{caseId}, #{fileName}, #{filePath}, #{fileType}, #{fileSize}, #{uploaderId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CaseMaterial m);

    @Select("SELECT * FROM case_material WHERE case_id = #{caseId}")
    List<CaseMaterial> findByCaseId(@Param("caseId") Long caseId);

    @Select("SELECT * FROM case_material WHERE id = #{id}")
    CaseMaterial findById(@Param("id") Long id);

    @Delete("DELETE FROM case_material WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM case_material WHERE case_id = #{caseId}")
    int countByCaseId(@Param("caseId") Long caseId);
}