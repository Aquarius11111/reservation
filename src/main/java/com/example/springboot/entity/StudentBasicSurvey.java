package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StudentBasicSurvey {
    private Long surveyId;
    private String studentId;
    private String name;
    private String gender;
    private Integer age;
    private String ethnicity;
    private String birthplace;
    private String religion;
    private String education;
    private String majorType;
    private String academicStatus;
    private String familyIncome;
    private String familyStructure;
    private String leftBehindExperience;
    private Integer siblingsNum;
    private Integer siblingsRank;
    private String relationFather;
    private String relationMother;
    private String relationSiblings;
    private Date createdAt;
}
