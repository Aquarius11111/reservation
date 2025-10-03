package com.example.springboot.dto;

import lombok.Data;

// 批量删除可预约时段响应DTO
@Data
public class ReserveTimeDeleteDTO {
    private int deletedTimeCount;
}
