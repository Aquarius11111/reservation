package com.example.springboot.service;

import com.example.springboot.utils.RESP;

public interface OverviewService {

    /** 预约数据概览（包含数量统计与记录） */
    RESP getReservationOverview();
    /** 咨询师匹配预约记录数据概览（包含数量统计与记录） */
    RESP getCounselorOverview(String counselorId);
    /** SCL-90 测评总览 */
    RESP getEvaluateOverview();
    /** 查询单个学生的测评总数和测评记录 */
    RESP getEvaluateByStudent(String studentId);
    /** 按月份统计预约数据 */
    RESP getMonthlyReservationOverview(String month);
}
