package com.example.springboot.dto;

import lombok.Data;

@Data
public class ReservationAddDTO {
    private String studentId;
    private String counselorId;
    private Long reserveTimeId;
    private String consultTopic;
}
