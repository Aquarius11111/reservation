package com.example.springboot.mapper;

import com.example.springboot.entity.StudentEvaluateResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentEvaluateResultMapper {

    @Insert("INSERT INTO student_evaluate_result(assessment_id, student_id, total_score, total_avg, " +
            "positive_item_num, positive_avg, somatization_score, obsessive_score, interpersonal_score, " +
            "depression_score, anxiety_score, hostility_score, phobic_score, paranoid_score, psychotic_score, other_score, created_at) " +
            "VALUES(#{assessmentId}, #{studentId}, #{totalScore}, #{totalAvg}, #{positiveItemNum}, #{positiveAvg}, " +
            "#{somatizationScore}, #{obsessiveScore}, #{interpersonalScore}, #{depressionScore}, #{anxietyScore}, " +
            "#{hostilityScore}, #{phobicScore}, #{paranoidScore}, #{psychoticScore}, #{otherScore}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    void insertResult(StudentEvaluateResult result);

    @Select("SELECT * FROM student_evaluate_result WHERE student_id = #{studentId}")
    List<StudentEvaluateResult> findResultByStudentId(String studentId);
}
