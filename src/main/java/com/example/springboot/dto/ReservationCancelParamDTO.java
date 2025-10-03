package com.example.springboot.dto;

import lombok.Data;

// 取消预约请求DTO
@Data
public class ReservationCancelParamDTO {
    private Long reserveId;
    private String cancelReason;
}
