package com.example.springboot.dto;

import lombok.Data;

// 创建咨询记录响应DTO
@Data
public class ConsultRecordCreateDTO {
    private Long recordId;
    private Long reserveId;
    private String createTime;
}
