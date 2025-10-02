package com.example.springboot.mapper;

import com.example.springboot.entity.Scl90Assessment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface Scl90Mapper {

    @Insert({
            "<script>",
            "INSERT INTO scl90_assessment(student_id, assessment_date, ",
            "<foreach collection='answers' item='ans' index='i' separator=','>",
            "q${i+1}",
            "</foreach>",
            ", total_score, created_at) VALUES (",
            "#{studentId}, #{assessmentDate}, ",
            "<foreach collection='answers' item='ans' index='i' separator=','>",
            "#{ans}",
            "</foreach>",
            ", #{totalScore}, #{createdAt})",
            "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "assessmentId")
    void insertAssessment(Scl90Assessment assessment);
}
