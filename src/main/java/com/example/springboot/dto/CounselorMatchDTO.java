package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

// 咨询师匹配结果DTO
@Data
public class CounselorMatchDTO {
    private String counselorId;
    private String counselorName;
    private String specialty;
    private String orientation;
    private String title;
    private String location;
    private String avatarUrl;
    private List<ReserveTimeDTO> matchedTimeList;
}
