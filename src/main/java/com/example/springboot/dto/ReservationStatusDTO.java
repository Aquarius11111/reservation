package com.example.springboot.dto;

import lombok.Data;

// 预约状态响应DTO
@Data
public class ReservationStatusDTO {
    private Long reserveId;
    private Integer reserveStatus;
    private String updateTime;
}
