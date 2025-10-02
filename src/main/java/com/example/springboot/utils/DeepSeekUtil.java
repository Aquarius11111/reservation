package com.example.springboot.utils;

import com.example.springboot.dto.DeepSeekResponse;
import com.example.springboot.entity.Scl90Assessment;
import com.example.springboot.entity.StudentEvaluateResult;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class DeepSeekUtil {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-950dd2dc51ab42108fa4f84dd64106a4"; // deepseek密钥

    public static DeepSeekResponse analyze(StudentEvaluateResult result, Scl90Assessment assessment) {
        try {
            // 1. 组织提示词，明确输出格式
            String prompt = "以下是学生SCL-90心理测评结果，请你用专业但温和的语言给出分析和建议：\n"
                    + "总分: " + result.getTotalScore() + "\n"
                    + "均分: " + result.getTotalAvg() + "\n"
                    + "阳性项目数: " + result.getPositiveItemNum() + "\n"
                    + "各因子得分: 抑郁=" + result.getDepressionScore()
                    + "，焦虑=" + result.getAnxietyScore()
                    + "，人际关系敏感=" + result.getInterpersonalScore()
                    + "...\n"
                    + "请严格按照以下格式输出：\n"
                    + "【分析】在此写心理状态分析\n"
                    + "【建议】在此写生活和心理建议\n"
                    + "请确保【分析】和【建议】都完整，不要截断内容。";

            // 2. 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat"); // 假设模型名
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{
                        put("role", "user");
                        put("content", prompt);
                    }}
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. 发送请求
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // 4. 解析返回结果
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 5. 按标签拆分内容
            DeepSeekResponse deepSeekResponse = new DeepSeekResponse();
            String analysis = "";
            String suggestion = "";

            if (content.contains("【分析】") && content.contains("【建议】")) {
                int analysisStart = content.indexOf("【分析】") + "【分析】".length();
                int suggestionStart = content.indexOf("【建议】");
                analysis = content.substring(analysisStart, suggestionStart).trim();
                suggestion = content.substring(suggestionStart + "【建议】".length()).trim();
            } else {
                // 如果 AI 没有输出标签，返回全部内容到 analysis
                analysis = content.trim();
                suggestion = "暂无建议";
            }

            deepSeekResponse.setAnalysis(analysis);
            deepSeekResponse.setSuggestion(suggestion);

            return deepSeekResponse;

        } catch (Exception e) {
            e.printStackTrace();
            DeepSeekResponse error = new DeepSeekResponse();
            error.setAnalysis("调用AI失败");
            error.setSuggestion("请稍后再试");
            return error;
        }
    }
}
