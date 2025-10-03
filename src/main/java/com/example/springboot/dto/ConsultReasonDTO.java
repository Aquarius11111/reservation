package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

/**
 * 咨询原因相关DTO
 */
// 咨询原因列表响应DTO
@Data
public class ConsultReasonDTO {
    private List<String> reasonList;
}
