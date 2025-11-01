package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Scl90Assessment {
    private Long assessmentId;
    private String studentId;
    private Date assessmentDate;
    //private Integer[] answers; // 90 个元素，对应 q1..q90
    private List<Integer> answers; // ✅ 改成 List<Integer>
    private Integer totalScore;
    private Date createdAt;
}
