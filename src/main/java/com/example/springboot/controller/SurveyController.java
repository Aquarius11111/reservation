package com.example.springboot.controller;

import com.example.springboot.dto.DeepSeekResponse;
import com.example.springboot.dto.FirstSurveyRequest;
import com.example.springboot.dto.RetestSurveyRequest;
import com.example.springboot.entity.StudentEvaluateResult;
import com.example.springboot.service.SurveyService;
import com.example.springboot.utils.DeepSeekUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/first")
    public ResponseEntity<?> firstSurvey(@RequestBody FirstSurveyRequest req) {
        return ResponseEntity.ok(surveyService.handleFirstSurvey(req));
    }

    @PostMapping("/retest")
    public ResponseEntity<?> retestSurvey(@RequestBody RetestSurveyRequest req) {
        return ResponseEntity.ok(surveyService.handleRetestSurvey(req));
    }

    @GetMapping("/result/{studentId}")
    public ResponseEntity<?> getResult(@PathVariable String studentId) {
        return ResponseEntity.ok(surveyService.getResultsByStudent(studentId));
    }

    // 4️⃣ 新增：查看某次报告详情
    @GetMapping("/result/detail")
    public ResponseEntity<?> getResultDetail(@RequestParam String studentId,
                                             @RequestParam Long resultId) {
        return ResponseEntity.ok(surveyService.getResultDetail(studentId, resultId));
    }

}
