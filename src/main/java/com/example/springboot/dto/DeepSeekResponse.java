package com.example.springboot.dto;

import lombok.Data;

@Data
public class DeepSeekResponse {
    private String analysis;  // 分析
    private String suggestion; // 建议
}
