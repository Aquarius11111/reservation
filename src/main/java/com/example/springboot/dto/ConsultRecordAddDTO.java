package com.example.springboot.dto;

import lombok.Data;

@Data
public class ConsultRecordAddDTO {
    private Long reserveId;
    private String counselorId;
    private String consultDate;

    private String studentPerformance;
    private String consultContent;

    private String advicePlan;
}
