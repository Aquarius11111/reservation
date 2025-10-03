package com.example.springboot.dto;

import lombok.Data;

// 咨询师匹配详情DTO（内部使用）
@Data
public class CounselorMatchDetailDTO {
    private String counselorId;
    private String counselorName;
    private String specialty;
    private String orientation;
    private String title;
    private String location;
    private String avatarUrl;
    private Long reserveTimeId;
    private String reserveDate;
    private String startTime;
    private String endTime;
    private Integer isOccupied;
}
