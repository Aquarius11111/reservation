package com.example.springboot.dto;

import lombok.Data;

// 时段DTO
@Data
public class ReserveTimeDTO {
    private Long reserveTimeId;
    private String reserveDate;
    private String startTime;
    private String endTime;
    private Integer isOccupied;
}
