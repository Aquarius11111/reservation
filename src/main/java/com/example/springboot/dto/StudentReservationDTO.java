package com.example.springboot.dto;

import lombok.Data;

/**
 * 预约记录查询相关DTO
 */
// 学生预约记录DTO
@Data
public class StudentReservationDTO {
    private Long reserveId;
    private String counselorName;
    private String reserveDate;
    private String reserveTime;
    private String consultTopic;
    private String consultRoom;
    private Integer reserveStatus;
    private String createTime;
}
