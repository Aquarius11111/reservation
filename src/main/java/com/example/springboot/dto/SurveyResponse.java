package com.example.springboot.dto;

import com.example.springboot.entity.StudentEvaluateResult;
import lombok.Data;

@Data
public class SurveyResponse {
    private StudentEvaluateResult result;   // 分数等计算结果
}

