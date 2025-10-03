package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

/**
 * 管理员排班相关DTO
 */
// 创建可预约时段请求DTO
@Data
public class ReserveTimeAddDTO {
    private String counselorId;
    private List<ReserveDateTimeDTO> reserveDateList;
}
