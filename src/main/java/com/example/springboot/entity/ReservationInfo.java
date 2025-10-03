package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ReservationInfo {

    private long reserveId;
    private String studentId;
    private String counselorId;

    /**
     * 预约日期（如 “2025-06-01”）
     */
    private Date reserveDate;

    /**
     * 预约时段（如 “10:00-11:00”）
     */
    private String reserveTime;

    /**
     * 咨询室地址（如 “心理健康中心 201 室”）
     */
    private String consultRoom;

    /**
     * 咨询原因（学生填写）
     */
    private String consultTopic;

    /**
     * 预约状态：1=已预约，2=已取消，3=已完成，4=已爽约
     */
    private Integer reserveStatus;

    /**
     * 取消原因（学生取消时填写，可选）
     */
    private String cancelReason;

    /**
     * 预约创建时间
     */
    private Date createTime;

    /**
     * 状态更新时间
     */
    private Date updateTime;
}
