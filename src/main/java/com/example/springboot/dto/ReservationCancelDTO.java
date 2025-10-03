package com.example.springboot.dto;

import lombok.Data;

// 取消预约响应DTO
@Data
public class ReservationCancelDTO {
    private Long reserveId;
    private Integer reserveStatus;
    private String updateTime;
}
