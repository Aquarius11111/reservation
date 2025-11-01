package com.example.springboot.service;

import com.example.springboot.dto.LoginResponseDTO;

/**
 * 用户服务接口
 */
public interface UserService {
    // 根据用户ID查询登录用户信息（不含密码）
    LoginResponseDTO getLoginUserByUserId(String userId);

    // 根据用户ID查询密码（用于校验）
    String getPasswordByUserId(String userId);
}