package com.example.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * 学生详细信息实体类
 */
@Data
public class StudentInfo {

    /** 学号，对应 sys_user.user_id */
    private String studentId;

    /** 性别：1=男，2=女，3=其他 */
    private Integer gender;

    /** 学院 */
    private String college;

    /** 年级 */
    private String grade;

    /** 专业 */
    private String major;

    /** 班级 */
    private String className;

    /** 手机号码 */
    private String phoneNumber;

    /** 心理风险等级：0=无风险 */
    private Integer riskLevel;

    /** 最近一次心理测评时间 */
    private LocalDateTime lastEvaluateTime;

    /** 最近一次心理咨询时间 */
    private LocalDateTime lastCounselTime;
}
