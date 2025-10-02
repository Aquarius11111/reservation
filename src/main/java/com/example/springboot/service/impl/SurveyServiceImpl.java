package com.example.springboot.service.impl;

import com.example.springboot.dto.DeepSeekResponse;
import com.example.springboot.dto.FirstSurveyRequest;
import com.example.springboot.dto.RetestSurveyRequest;
import com.example.springboot.dto.SurveyResponse;
import com.example.springboot.entity.Scl90Assessment;
import com.example.springboot.entity.StudentBasicSurvey;
import com.example.springboot.entity.StudentEvaluateResult;
import com.example.springboot.mapper.Scl90Mapper;
import com.example.springboot.mapper.StudentBasicSurveyMapper;
import com.example.springboot.mapper.StudentEvaluateResultMapper;
import com.example.springboot.service.SurveyService;
import com.example.springboot.utils.Scl90Calculator;
import com.example.springboot.utils.DeepSeekUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;  // ✅ 改成 java.sql.Date，避免 setCreatedAt 报错
import java.util.List;

@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private StudentBasicSurveyMapper studentBasicSurveyMapper;

    @Autowired
    private Scl90Mapper scl90Mapper;

    @Autowired
    private StudentEvaluateResultMapper resultMapper;

    @Override
    public SurveyResponse handleFirstSurvey(FirstSurveyRequest req) {
        // 1. 保存基本信息
        StudentBasicSurvey survey = req.getBasicSurvey();
        survey.setCreatedAt(new Date(System.currentTimeMillis()));
        studentBasicSurveyMapper.insert(survey);

        // 2. 保存 SCL-90 答卷
        Scl90Assessment assessment = req.getScl90Assessment();
        assessment.setAssessmentDate(new Date(System.currentTimeMillis()));
        assessment.setCreatedAt(new Date(System.currentTimeMillis()));
        scl90Mapper.insertAssessment(assessment);

        // 3. 计算得分
        StudentEvaluateResult result = Scl90Calculator.calculate(assessment);
        result.setStudentId(assessment.getStudentId());
        result.setAssessmentId(assessment.getAssessmentId());
        result.setCreatedAt(new Date(System.currentTimeMillis()));
        resultMapper.insertResult(result);  // 此时所有字段都不为 null

        // 4. 调用 AI
        DeepSeekResponse ds = DeepSeekUtil.analyze(result, assessment);

        // 5. 封装返回
        SurveyResponse response = new SurveyResponse();
        response.setResult(result);
        response.setAnalysis(ds.getAnalysis());
        response.setSuggestion(ds.getSuggestion());

        return response;
    }

    @Override
    public SurveyResponse handleRetestSurvey(RetestSurveyRequest req) {
        // 1. 保存 SCL-90 答卷
        Scl90Assessment assessment = req.getScl90Assessment();
        assessment.setAssessmentDate(new Date(System.currentTimeMillis()));
        assessment.setCreatedAt(new Date(System.currentTimeMillis()));
        scl90Mapper.insertAssessment(assessment);

        // 2. 计算得分
        StudentEvaluateResult result = Scl90Calculator.calculate(assessment);
        result.setStudentId(assessment.getStudentId());
        result.setAssessmentId(assessment.getAssessmentId());
        result.setCreatedAt(new Date(System.currentTimeMillis()));
        resultMapper.insertResult(result);  // 此时所有字段都不为 null

        // 3. 调用 AI
        DeepSeekResponse ds = DeepSeekUtil.analyze(result, assessment);

        // 4. 封装返回
        SurveyResponse response = new SurveyResponse();
        response.setResult(result);
        response.setAnalysis(ds.getAnalysis());
        response.setSuggestion(ds.getSuggestion());

        return response;
    }

    @Override
    public List<StudentEvaluateResult> getResultsByStudent(String studentId) {
        return resultMapper.findResultByStudentId(studentId);
    }
}
