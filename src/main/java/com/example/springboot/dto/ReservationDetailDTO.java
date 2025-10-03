package com.example.springboot.dto;

import lombok.Data;

// 预约详情DTO
@Data
public class ReservationDetailDTO {
    private Long reserveId;
    private String counselorName;
    private String counselorSpecialty;
    private String reserveDate;
    private String reserveTime;
    private String consultTopic;
    private String consultRoom;
    private Integer reserveStatus;
    private String createTime;
    private String updateTime;
}
