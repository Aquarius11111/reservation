package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StudentEvaluateResult {
    private Long resultId;
    private Long assessmentId;
    private String studentId;
    private Integer totalScore;
    private Double totalAvg;
    private Integer positiveItemNum;
    private Double positiveAvg;
    private Integer somatizationScore;
    private Integer obsessiveScore;
    private Integer interpersonalScore;
    private Integer depressionScore;
    private Integer anxietyScore;
    private Integer hostilityScore;
    private Integer phobicScore;
    private Integer paranoidScore;
    private Integer psychoticScore;
    private Integer otherScore;
    private Date createdAt;
    // 🧠 新增字段：AI分析结果与建议
    private String analysis;     // AI分析结果（心理状态解读）
    private String suggestion;   // AI生成的心理建议与调节方案
}

