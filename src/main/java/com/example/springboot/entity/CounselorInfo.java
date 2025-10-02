package com.example.springboot.entity;


import lombok.Data;

/**
 * 咨询师详细信息实体
 */
@Data
public class CounselorInfo {

    /**
     * 咨询师唯一标识（关联 sys_user.user_id）
     */
    private String counselorId;

    /**
     * 专长（如“学业压力，情感困扰”）
     */
    private String specialty;

    /**
     * 咨询流派
     */
    private String orientation;

    /**
     * 职称
     */
    private String title;

    /**
     * 咨询工作地点
     */
    private String location;

    /**
     * 常规服务时间
     */
    private String serviceTime;

    /**
     * 寄语
     */
    private String motto;

    /**
     * 头像存储路径
     */
    private String avatarUrl;
}
