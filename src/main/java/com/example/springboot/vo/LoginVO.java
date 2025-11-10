package com.example.springboot.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder  // 便于构建响应对象
public class LoginVO {
    private String userId;      // 用户ID
    private String userName;    // 用户名
    private Integer userRole;   // 用户角色（1-管理员，2-普通用户等）
}
