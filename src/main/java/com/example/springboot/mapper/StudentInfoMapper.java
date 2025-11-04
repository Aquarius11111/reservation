package com.example.springboot.mapper;

import com.example.springboot.entity.StudentInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentInfoMapper {

    /** 新增学生信息 */
    @Insert("<script>"
            + "INSERT INTO student_info(student_id, gender, college, grade, major, class_name, phone_number, risk_level"
            + "<if test='lastEvaluateTime != null'>, last_evaluate_time</if>"
            + "<if test='lastCounselTime != null'>, last_counsel_time</if>) "
            + "VALUES(#{studentId}, #{gender}, #{college}, #{grade}, #{major}, #{className}, #{phoneNumber}, #{riskLevel}"
            + "<if test='lastEvaluateTime != null'>, #{lastEvaluateTime}</if>"
            + "<if test='lastCounselTime != null'>, #{lastCounselTime}</if>)"
            + "</script>")
    int insert(StudentInfo student);


    /** 删除学生信息 */
    @Delete("DELETE FROM student_info WHERE student_id = #{studentId}")
    int deleteById(@Param("studentId") String studentId);

    /** 更新学生信息 */
    @Update("UPDATE student_info SET gender=#{gender}, college=#{college}, grade=#{grade}, major=#{major}, class_name=#{className}, phone_number=#{phoneNumber}, " +
            "risk_level=#{riskLevel}, last_evaluate_time=#{lastEvaluateTime}, last_counsel_time=#{lastCounselTime} WHERE student_id=#{studentId}")
    int update(StudentInfo student);

    /** 查询单个学生信息 */
    @Select("SELECT * FROM student_info WHERE student_id = #{studentId}")
    StudentInfo selectById(@Param("studentId") String studentId);

    /** 查询所有学生信息 */
    @Select("SELECT * FROM student_info ORDER BY student_id ASC")
    List<StudentInfo> selectAll();

    /** 分页查询 */
    @Select("SELECT * FROM student_info ORDER BY student_id ASC LIMIT #{offset}, #{limit}")
    List<StudentInfo> selectPage(@Param("offset") int offset, @Param("limit") int limit);




    /** 多条件模糊搜索 + 分页（联表返回姓名） */
    @Select("<script>"
            + "SELECT s.*, u.user_name AS userName "
            + "FROM student_info s "
            + "LEFT JOIN sys_user u ON s.student_id = u.user_id "
            + "WHERE 1=1 "
            + "<if test='studentId != null and studentId != \"\"'> "
            + "AND s.student_id LIKE CONCAT('%', #{studentId}, '%') "
            + "</if>"
            + "<if test='gender != null'> "
            + "AND s.gender = #{gender} "
            + "</if>"
            + "<if test='college != null and college != \"\"'> "
            + "AND s.college LIKE CONCAT('%', #{college}, '%') "
            + "</if>"
            + "<if test='major != null and major != \"\"'> "
            + "AND s.major LIKE CONCAT('%', #{major}, '%') "
            + "</if>"
            + "<if test='className != null and className != \"\"'> "
            + "AND s.class_name LIKE CONCAT('%', #{className}, '%') "
            + "</if>"
            + "<if test='riskLevel != null'> "
            + "AND s.risk_level = #{riskLevel} "
            + "</if>"
            + "ORDER BY s.student_id ASC "
            + "LIMIT #{offset}, #{limit}"
            + "</script>")
    List<StudentInfo> searchPage(@Param("studentId") String studentId,
                                 @Param("gender") Integer gender,
                                 @Param("college") String college,
                                 @Param("major") String major,
                                 @Param("className") String className,
                                 @Param("riskLevel") Integer riskLevel,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);


    /** 多条件模糊搜索统计总数 */
    @Select("<script>"
            + "SELECT COUNT(*) "
            + "FROM student_info s "
            + "LEFT JOIN sys_user u ON s.student_id = u.user_id "
            + "WHERE 1=1 "
            + "<if test='studentId != null and studentId != \"\"'> "
            + "AND s.student_id LIKE CONCAT('%', #{studentId}, '%') "
            + "</if>"
            + "<if test='gender != null'> "
            + "AND s.gender = #{gender} "
            + "</if>"
            + "<if test='college != null and college != \"\"'> "
            + "AND s.college LIKE CONCAT('%', #{college}, '%') "
            + "</if>"
            + "<if test='major != null and major != \"\"'> "
            + "AND s.major LIKE CONCAT('%', #{major}, '%') "
            + "</if>"
            + "<if test='className != null and className != \"\"'> "
            + "AND s.class_name LIKE CONCAT('%', #{className}, '%') "
            + "</if>"
            + "<if test='riskLevel != null'> "
            + "AND s.risk_level = #{riskLevel} "
            + "</if>"
            + "</script>")
    int countSearch(@Param("studentId") String studentId,
                    @Param("gender") Integer gender,
                    @Param("college") String college,
                    @Param("major") String major,
                    @Param("className") String className,
                    @Param("riskLevel") Integer riskLevel);

    /** 总数 */
    @Select("SELECT COUNT(*) FROM student_info")
    int countAll();
}
