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
}
