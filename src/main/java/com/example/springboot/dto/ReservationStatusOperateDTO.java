package com.example.springboot.dto;

import lombok.Data;

// 预约状态操作DTO
@Data
public class ReservationStatusOperateDTO {
    private Long reserveId;
    private String counselorId;
}
