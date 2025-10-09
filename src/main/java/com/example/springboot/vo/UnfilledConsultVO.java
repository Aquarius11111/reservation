package com.example.springboot.vo;

import lombok.Data;

@Data
public class UnfilledConsultVO {
    private Long reserveId;     // 预约ID
    private Long studentId;     // 学生ID
    private String studentName; // 学生姓名
    private String reserveDate; // 预约日期
    private String createTime;  // 创建时间
}