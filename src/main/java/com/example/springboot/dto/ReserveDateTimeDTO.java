package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

// 日期-时段列表DTO
@Data
public class ReserveDateTimeDTO {
    private String reserveDate;
    private List<TimeSlotDTO> timeList;
}
