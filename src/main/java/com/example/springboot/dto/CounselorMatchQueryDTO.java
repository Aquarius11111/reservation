package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

/**
 * 咨询师匹配相关DTO
 */
// 咨询师匹配查询DTO
@Data
public class CounselorMatchQueryDTO {
    private List<SelectedTimeDTO> selectedTimeList; // 学生选择的多个时段
    private String consultReason; // 咨询原因
    private int pageNum = 1; // 页码
    private int pageSize = 10; // 每页条数
}