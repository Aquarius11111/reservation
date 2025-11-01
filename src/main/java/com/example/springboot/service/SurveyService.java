package com.example.springboot.service;

import com.example.springboot.dto.FirstSurveyRequest;
import com.example.springboot.dto.RetestSurveyRequest;
import com.example.springboot.dto.SurveyResponse;
import com.example.springboot.entity.StudentEvaluateResult;

import java.util.List;

public interface SurveyService {
    SurveyResponse handleFirstSurvey(FirstSurveyRequest req);
    SurveyResponse handleRetestSurvey(RetestSurveyRequest req);
    List<StudentEvaluateResult> getResultsByStudent(String studentId);
    StudentEvaluateResult getResultDetail(String studentId, Long resultId);
}


