package com.example.springboot.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {
    private String userId;       // 用户唯一标识（学号/工号/自定义）
    private String userName;     // 真实姓名
    private Integer userRole;    // 角色：1=学生, 2=咨询师, 3=管理员
    private String deptName;     // 所属部门/学院
    private String password;     // 密码（MD5加密后存储）
    private Integer status;      // 账号状态：1=正常, 0=禁用
    private String createUser;   // 账号创建人
    private LocalDateTime createTime;  // 账号创建时间
    private LocalDateTime updateTime;  // 账号最后更新时间
}
