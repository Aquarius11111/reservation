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

import java.util.Date; // ✅ 改成 java.sql.Date，避免 setCreatedAt 报错
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
        survey.setCreatedAt(new Date());
        studentBasicSurveyMapper.insert(survey);

        // 2. 保存SCL-90答卷
        Scl90Assessment assessment = req.getScl90Assessment();
        assessment.setAssessmentDate(new Date());
        assessment.setCreatedAt(new Date());
        scl90Mapper.insertAssessment(assessment);

        // 3. 计算得分
        StudentEvaluateResult result = Scl90Calculator.calculate(assessment);
        result.setStudentId(assessment.getStudentId());
        result.setAssessmentId(assessment.getAssessmentId());
        result.setCreatedAt(new Date());

        // 4. 调用AI生成分析与建议
        DeepSeekResponse ds = DeepSeekUtil.analyze(result, assessment);
        result.setAnalysis(ds.getAnalysis());
        result.setSuggestion(ds.getSuggestion());

        // 5. 存入数据库
        resultMapper.insertResult(result);

        // 6. 返回封装结果（只返回一次结果对象即可）
        SurveyResponse response = new SurveyResponse();
        response.setResult(result);
        return response;
    }

    @Override
    public SurveyResponse handleRetestSurvey(RetestSurveyRequest req) {
        // 1. 保存SCL-90答卷
        Scl90Assessment assessment = req.getScl90Assessment();
        assessment.setAssessmentDate(new Date());
        assessment.setCreatedAt(new Date());
        scl90Mapper.insertAssessment(assessment);

        // 2. 计算得分
        StudentEvaluateResult result = Scl90Calculator.calculate(assessment);
        result.setStudentId(assessment.getStudentId());
        result.setAssessmentId(assessment.getAssessmentId());
        result.setCreatedAt(new Date());

        // 3. 调用AI生成分析与建议
        DeepSeekResponse ds = DeepSeekUtil.analyze(result, assessment);
        result.setAnalysis(ds.getAnalysis());
        result.setSuggestion(ds.getSuggestion());

        // 4. 判断是否已有记录（有则更新，无则新增）
        StudentEvaluateResult existing = resultMapper.findLatestByStudentId(assessment.getStudentId());
        if (existing == null) {
            resultMapper.insertResult(result);
        } else {
            resultMapper.updateResult(result);
        }

        // 5. 返回封装结果
        SurveyResponse response = new SurveyResponse();
        response.setResult(result);
        return response;
    }

    @Override
    public List<StudentEvaluateResult> getResultsByStudent(String studentId) {
        return resultMapper.findResultByStudentId(studentId);
    }
}
