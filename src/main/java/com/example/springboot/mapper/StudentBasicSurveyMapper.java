package com.example.springboot.mapper;

import com.example.springboot.entity.StudentBasicSurvey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentBasicSurveyMapper {

    @Insert("INSERT INTO student_basic_survey(student_id, name, gender, age, ethnicity, birthplace, religion, " +
            "education, major_type, academic_status, family_income, family_structure, left_behind_experience, " +
            "siblings_num, siblings_rank, relation_father, relation_mother, relation_siblings, created_at) " +
            "VALUES(#{studentId}, #{name}, #{gender}, #{age}, #{ethnicity}, #{birthplace}, #{religion}, " +
            "#{education}, #{majorType}, #{academicStatus}, #{familyIncome}, #{familyStructure}, #{leftBehindExperience}, " +
            "#{siblingsNum}, #{siblingsRank}, #{relationFather}, #{relationMother}, #{relationSiblings}, #{createdAt})")
    void insert(StudentBasicSurvey survey);
}
