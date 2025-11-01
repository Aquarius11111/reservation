package com.example.springboot.dto;

import lombok.Data;

@Data
public class ConsultRecordQueryDTO {
    private Integer pageNum = 1;         // 页码
    private Integer pageSize = 10;       // 每页条数
    private Integer offset;              // 分页偏移量（计算得出）

    private String studentName;              // 学生ID
    private String counselorId;          // 咨询师ID
    private String consultDate;          // 咨询日期
    private String startDate;            // 开始日期
    private String endDate;              // 结束日期
}