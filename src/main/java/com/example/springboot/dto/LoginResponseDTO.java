package com.example.springboot.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功响应数据传输对象
 */
@Data
public class LoginResponseDTO {
    private String userId;      // 用户ID
    private String userName;    // 用户名
    private Integer userRole;   // 用户角色（1-管理员，2-普通用户等）
    private String lastEvaluateTime;
    private String lastCounselTime;

    public LoginResponseDTO() {
    }

}