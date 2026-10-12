package com.example.filing.mapper;

import com.example.filing.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    SysUser findById(@Param("id") Long id);


    @Select("SELECT * FROM sys_user")
    List<SysUser> findAll();

    @Update("UPDATE sys_user SET enabled = 0 WHERE id = #{id}")
    int disable(@Param("id") Long id);
}