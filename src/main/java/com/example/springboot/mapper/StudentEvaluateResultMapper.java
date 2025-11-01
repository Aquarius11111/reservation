package com.example.springboot.mapper;

import com.example.springboot.entity.StudentEvaluateResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentEvaluateResultMapper {

    // 插入测评结果（包含AI分析结果与建议）
    @Insert("INSERT INTO student_evaluate_result(" +
            "assessment_id, student_id, total_score, total_avg, positive_item_num, positive_avg, " +
            "somatization_score, obsessive_score, interpersonal_score, depression_score, anxiety_score, " +
            "hostility_score, phobic_score, paranoid_score, psychotic_score, other_score, created_at, " +
            "analysis, suggestion" +
            ") VALUES (" +
            "#{assessmentId}, #{studentId}, #{totalScore}, #{totalAvg}, #{positiveItemNum}, #{positiveAvg}, " +
            "#{somatizationScore}, #{obsessiveScore}, #{interpersonalScore}, #{depressionScore}, #{anxietyScore}, " +
            "#{hostilityScore}, #{phobicScore}, #{paranoidScore}, #{psychoticScore}, #{otherScore}, #{createdAt}, " +
            "#{analysis}, #{suggestion})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    void insertResult(StudentEvaluateResult result);

    // 根据 studentId 查询所有结果（历史记录）
    @Select("SELECT * FROM student_evaluate_result WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<StudentEvaluateResult> findResultByStudentId(String studentId);

    // 根据 studentId 查询最新一条结果
    @Select("SELECT * FROM student_evaluate_result WHERE student_id = #{studentId} ORDER BY created_at DESC LIMIT 1")
    StudentEvaluateResult findLatestByStudentId(String studentId);

    // ✅ 新增：根据 studentId 和 resultId 查询单次测评详情
    @Select("SELECT * FROM student_evaluate_result WHERE student_id = #{studentId} AND result_id = #{resultId}")
    StudentEvaluateResult findResultDetail(@Param("studentId") String studentId, @Param("resultId") Long resultId);

    // 更新测评结果（如果使用覆盖模式）
    @Update("UPDATE student_evaluate_result SET " +
            "total_score = #{totalScore}, total_avg = #{totalAvg}, positive_item_num = #{positiveItemNum}, " +
            "positive_avg = #{positiveAvg}, somatization_score = #{somatizationScore}, obsessive_score = #{obsessiveScore}, " +
            "interpersonal_score = #{interpersonalScore}, depression_score = #{depressionScore}, anxiety_score = #{anxietyScore}, " +
            "hostility_score = #{hostilityScore}, phobic_score = #{phobicScore}, paranoid_score = #{paranoidScore}, " +
            "psychotic_score = #{psychoticScore}, other_score = #{otherScore}, created_at = #{createdAt}, " +
            "analysis = #{analysis}, suggestion = #{suggestion} " +
            "WHERE student_id = #{studentId}")
    void updateResult(StudentEvaluateResult result);
}
