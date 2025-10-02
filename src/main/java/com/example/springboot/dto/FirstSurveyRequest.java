package com.example.springboot.dto;

import com.example.springboot.entity.Scl90Assessment;
import com.example.springboot.entity.StudentBasicSurvey;
import lombok.Data;

@Data
public class FirstSurveyRequest {
    private StudentBasicSurvey basicSurvey;
    private Scl90Assessment scl90Assessment;
}
