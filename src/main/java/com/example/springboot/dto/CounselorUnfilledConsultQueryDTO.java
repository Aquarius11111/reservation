package com.example.springboot.dto;

import lombok.Data;

@Data
public class CounselorUnfilledConsultQueryDTO {
    private Integer pageNum = 1;    // 页码，默认1
    private Integer pageSize = 10;  // 每页条数，默认10
    private Integer offset;         // 分页偏移量（后端计算）
    private String counselorId;     // 咨询师ID（必填）
    private String startDate;       // 开始日期，格式：yyyy-MM-dd
    private String endDate;         // 结束日期，格式：yyyy-MM-dd
}