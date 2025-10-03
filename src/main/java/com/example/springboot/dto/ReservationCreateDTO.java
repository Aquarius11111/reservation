package com.example.springboot.dto;

import lombok.Data;

// 创建预约响应DTO
@Data
public class ReservationCreateDTO {
    private Long reserveId;
    private String reserveDate;
    private String reserveTime;
    private String counselorName;
    private String consultRoom;
    private Integer reserveStatus;
    private String createTime;
}
