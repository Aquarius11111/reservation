package com.example.springboot.dto;

import lombok.Data;

/**
 * 登录请求数据传输对象
 */
@Data
public class LoginRequestDTO {
    private String userId;      // 登录用的用户ID

    private String password;    // 登录密码
}