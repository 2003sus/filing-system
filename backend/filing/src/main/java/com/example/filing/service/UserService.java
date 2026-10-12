package com.example.filing.service;

import com.example.filing.entity.SysUser;
import com.example.filing.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private SysUserMapper userMapper;

    public SysUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public SysUser findById(Long id) {
        return userMapper.findById(id);
    }
}