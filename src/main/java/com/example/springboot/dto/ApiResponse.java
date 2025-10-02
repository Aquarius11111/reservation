package com.example.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;    // 业务状态码
    private String msg;  // 信息描述
    private T data;      // 返回的数据
    private Long total;  // 可选：分页总数
}
