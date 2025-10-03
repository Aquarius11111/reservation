package com.example.springboot.dto;

import com.sun.istack.internal.NotNull;
import lombok.Data;

// 学生选中的时段DTO
@Data
public class SelectedTimeDTO {
    private String reserveDate; // 预约日期
    private String startTime; // 开始时间
    private String endTime; // 结束时间
}