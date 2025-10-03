package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

/**
 * 时段相关DTO
 */
// 日期-时段组合DTO
@Data
public class DateFreeTimeDTO {
    private String reserveDate;
    private List<ReserveTimeDTO> timeList;
}
