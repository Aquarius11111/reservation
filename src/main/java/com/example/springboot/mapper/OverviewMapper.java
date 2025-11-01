package com.example.springboot.mapper;

import com.example.springboot.entity.ReservationInfo;
import com.example.springboot.entity.StudentEvaluateResult;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OverviewMapper {

    /** 今日已预约数（根据预约日期） */
    @Select("SELECT COUNT(*) FROM reservation_info WHERE reserve_date = CURDATE() AND reserve_status = 1")
    int getTodayReservationCount();

    /** 全部预约数 */
    @Select("SELECT COUNT(*) FROM reservation_info")
    int getAllReservationCount();

    /** 按状态统计预约数量 */
    @Select("SELECT COUNT(*) FROM reservation_info WHERE reserve_status = 1")
    int getReservedCount();   // 已预约

    @Select("SELECT COUNT(*) FROM reservation_info WHERE reserve_status = 2")
    int getCanceledCount();   // 已取消

    @Select("SELECT COUNT(*) FROM reservation_info WHERE reserve_status = 3")
    int getCompletedCount();  // 已完成

    @Select("SELECT COUNT(*) FROM reservation_info WHERE reserve_status = 4")
    int getNoShowCount();     // 已爽约

    /** 返回所有预约记录（按预约日期排序） */
    @Select("SELECT * FROM reservation_info ORDER BY reserve_date DESC, reserve_time ASC")
    List<ReservationInfo> getAllReservations();

    /** 返回今日预约记录（仅状态为“已预约”的） */
    @Select("SELECT * FROM reservation_info WHERE reserve_date = CURDATE() AND reserve_status = 1 ORDER BY reserve_time ASC")
    List<ReservationInfo> getTodayReservations();


    /** 咨询记录--查询单个咨询师匹配的预约记录Mapper */
    // 查询某咨询师今日的预约记录（按预约日期，不按创建时间）
    @Select("SELECT * FROM reservation_info WHERE counselor_id = #{counselorId} AND reserve_date = CURDATE() AND reserve_status = 1 ORDER BY reserve_time ASC")
    List<ReservationInfo> getTodayReservationsByCounselor(String counselorId);



    // 查询某咨询师的所有预约记录
    @Select("SELECT * FROM reservation_info WHERE counselor_id = #{counselorId} ORDER BY create_time DESC")
    List<ReservationInfo> getAllReservationsByCounselor(String counselorId);

    // 查询某咨询师的各状态记录
    @Select("SELECT * FROM reservation_info WHERE counselor_id = #{counselorId} AND reserve_status = 3 ORDER BY create_time DESC")
    List<ReservationInfo> getCompletedReservationsByCounselor(String counselorId);

    @Select("SELECT * FROM reservation_info WHERE counselor_id = #{counselorId} AND reserve_status = 2 ORDER BY create_time DESC")
    List<ReservationInfo> getCanceledReservationsByCounselor(String counselorId);

    @Select("SELECT * FROM reservation_info WHERE counselor_id = #{counselorId} AND reserve_status = 4 ORDER BY create_time DESC")
    List<ReservationInfo> getNoShowReservationsByCounselor(String counselorId);

    /** 测评结果--查询所有测评结果数据统计Mapper */
    // 总测评数
    @Select("SELECT COUNT(*) FROM student_evaluate_result")
    int getTotalEvaluateCount();

    // 各分数区间测评数
    @Select("SELECT COUNT(*) FROM student_evaluate_result WHERE total_score <= 160")
    int getScoreBelow160Count();

    @Select("SELECT COUNT(*) FROM student_evaluate_result WHERE total_score > 160 AND total_score <= 225")
    int getScore160To225Count();

    @Select("SELECT COUNT(*) FROM student_evaluate_result WHERE total_score > 225 AND total_score <= 315")
    int getScore226To315Count();

    @Select("SELECT COUNT(*) FROM student_evaluate_result WHERE total_score > 315")
    int getScoreAbove315Count();

    // 各分数区间对应的记录
    @Select("SELECT * FROM student_evaluate_result WHERE total_score <= 160 ORDER BY created_at DESC")
    List<StudentEvaluateResult> getRecordsBelow160();

    @Select("SELECT * FROM student_evaluate_result WHERE total_score > 160 AND total_score <= 225 ORDER BY created_at DESC")
    List<StudentEvaluateResult> getRecords160To225();

    @Select("SELECT * FROM student_evaluate_result WHERE total_score > 225 AND total_score <= 315 ORDER BY created_at DESC")
    List<StudentEvaluateResult> getRecords226To315();

    @Select("SELECT * FROM student_evaluate_result WHERE total_score > 315 ORDER BY created_at DESC")
    List<StudentEvaluateResult> getRecordsAbove315();

    // 统计某个学生的测评总数
    @Select("SELECT COUNT(*) FROM student_evaluate_result WHERE student_id = #{studentId}")
    int getEvaluateCountByStudent(String studentId);

    // 查询某个学生的测评记录（返回全部字段）
    @Select("SELECT * FROM student_evaluate_result WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<StudentEvaluateResult> getEvaluateRecordsByStudent(String studentId);

    /** 统计指定月份的预约总数 */
    @Select("SELECT COUNT(*) FROM reservation_info " +
            "WHERE DATE_FORMAT(reserve_date, '%Y-%m') = #{month}")
    int getMonthlyReservationCount(String month);

    /** 统计指定月份各状态的预约数 */
    @Select("SELECT COUNT(*) FROM reservation_info " +
            "WHERE DATE_FORMAT(reserve_date, '%Y-%m') = #{month} AND reserve_status = 1")
    int getMonthlyReservedCount(String month);

    @Select("SELECT COUNT(*) FROM reservation_info " +
            "WHERE DATE_FORMAT(reserve_date, '%Y-%m') = #{month} AND reserve_status = 2")
    int getMonthlyCanceledCount(String month);

    @Select("SELECT COUNT(*) FROM reservation_info " +
            "WHERE DATE_FORMAT(reserve_date, '%Y-%m') = #{month} AND reserve_status = 3")
    int getMonthlyCompletedCount(String month);

    @Select("SELECT COUNT(*) FROM reservation_info " +
            "WHERE DATE_FORMAT(reserve_date, '%Y-%m') = #{month} AND reserve_status = 4")
    int getMonthlyNoShowCount(String month);
}
