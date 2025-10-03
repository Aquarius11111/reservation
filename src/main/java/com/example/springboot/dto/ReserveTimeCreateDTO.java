package com.example.springboot.dto;

import lombok.Data;

// 创建可预约时段响应DTO
@Data
public class ReserveTimeCreateDTO {
    private int createdTimeCount;
    private String counselorId;
}
