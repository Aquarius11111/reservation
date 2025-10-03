package com.example.springboot.dto;

import lombok.Data;

// 咨询师预约记录DTO
@Data
public class CounselorReservationDTO {
    private Long reserveId;
    private String studentName;
    private String studentId;
    private String studentCollege;
    private String reserveDate;
    private String reserveTime;
    private String consultTopic;
    private Integer reserveStatus;
    private String createTime;
}
