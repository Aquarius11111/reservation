package com.example.springboot.service.impl;

import com.example.springboot.dto.LoginResponseDTO;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponseDTO getLoginUserByUserId(String userId) {
        return userMapper.selectLoginUserByUserId(userId);
    }

    @Override
    public String getPasswordByUserId(String userId) {
        return userMapper.selectPasswordByUserId(userId);
    }
}