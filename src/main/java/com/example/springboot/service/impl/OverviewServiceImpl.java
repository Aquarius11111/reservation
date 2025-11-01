package com.example.springboot.service.impl;

import com.example.springboot.entity.ReservationInfo;
import com.example.springboot.entity.StudentEvaluateResult;
import com.example.springboot.mapper.OverviewMapper;
import com.example.springboot.service.OverviewService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OverviewServiceImpl implements OverviewService {

    @Autowired
    private OverviewMapper overviewMapper;

    @Override
    public RESP getReservationOverview() {
        try {
            // 基础数量统计
            int todayCount = overviewMapper.getTodayReservationCount();
            int allCount = overviewMapper.getAllReservationCount();
            int reservedCount = overviewMapper.getReservedCount();
            int canceledCount = overviewMapper.getCanceledCount();
            int completedCount = overviewMapper.getCompletedCount();
            int noShowCount = overviewMapper.getNoShowCount();

            // 获取对应记录
            List<ReservationInfo> todayList = overviewMapper.getTodayReservations();
            List<ReservationInfo> allList = overviewMapper.getAllReservations();

            // 封装返回数据
            Map<String, Object> countMap = new HashMap<>();
            countMap.put("todayCount", todayCount);
            countMap.put("allCount", allCount);
            countMap.put("reservedCount", reservedCount);
            countMap.put("canceledCount", canceledCount);
            countMap.put("completedCount", completedCount);
            countMap.put("noShowCount", noShowCount);

            // 使用 RESP 封装（数量 + 记录）
            return RESP.ok(countMap, todayList, allList);

        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("查询数据概览失败：" + e.getMessage());
        }
    }

    @Override
    public RESP getCounselorOverview(String counselorId) {
        Map<String, Object> result = new HashMap<>();

        // 获取各类预约记录
        List<ReservationInfo> todayList = overviewMapper.getTodayReservationsByCounselor(counselorId);
        List<ReservationInfo> allList = overviewMapper.getAllReservationsByCounselor(counselorId);
        List<ReservationInfo> completedList = overviewMapper.getCompletedReservationsByCounselor(counselorId);
        List<ReservationInfo> canceledList = overviewMapper.getCanceledReservationsByCounselor(counselorId);
        List<ReservationInfo> noShowList = overviewMapper.getNoShowReservationsByCounselor(counselorId);

        // 统计数量
        result.put("todayCount", todayList.size());
        result.put("allCount", allList.size());
        result.put("completedCount", completedList.size());
        result.put("canceledCount", canceledList.size());
        result.put("noShowCount", noShowList.size());

        // 附带各类型的预约详情
        result.put("todayList", todayList);
        result.put("allList", allList);
        result.put("completedList", completedList);
        result.put("canceledList", canceledList);
        result.put("noShowList", noShowList);

        return RESP.ok(result);
    }

    @Override
    public RESP getEvaluateOverview() {
        try {
            // 获取统计数量
            int totalCount = overviewMapper.getTotalEvaluateCount();
            int below160 = overviewMapper.getScoreBelow160Count();
            int score160To225 = overviewMapper.getScore160To225Count();
            int score226To315 = overviewMapper.getScore226To315Count();
            int above315 = overviewMapper.getScoreAbove315Count();

            // 获取对应记录
            List<StudentEvaluateResult> listBelow160 = overviewMapper.getRecordsBelow160();
            List<StudentEvaluateResult> list160To225 = overviewMapper.getRecords160To225();
            List<StudentEvaluateResult> list226To315 = overviewMapper.getRecords226To315();
            List<StudentEvaluateResult> listAbove315 = overviewMapper.getRecordsAbove315();

            // 封装数量
            Map<String, Object> countMap = new HashMap<>();
            countMap.put("totalCount", totalCount);
            countMap.put("below160Count", below160);
            countMap.put("score160To225Count", score160To225);
            countMap.put("score226To315Count", score226To315);
            countMap.put("above315Count", above315);

            // 返回 RESP，data 为数量，data1-data4 为各类记录
            return RESP.ok(countMap, listBelow160, list160To225, list226To315);
            // 如果需要把 listAbove315 也返回，可以使用 data3 或者自定义 RESP
        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("查询测评概览失败：" + e.getMessage());
        }
    }

    @Override
    public RESP getEvaluateByStudent(String studentId) {
        try {
            int totalCount = overviewMapper.getEvaluateCountByStudent(studentId);
            List<StudentEvaluateResult> records = overviewMapper.getEvaluateRecordsByStudent(studentId);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("records", records);

            return RESP.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("查询学生测评数据失败：" + e.getMessage());
        }
    }

    @Override
    public RESP getMonthlyReservationOverview(String month) {
        try {
            int totalCount = overviewMapper.getMonthlyReservationCount(month);
            int reservedCount = overviewMapper.getMonthlyReservedCount(month);
            int canceledCount = overviewMapper.getMonthlyCanceledCount(month);
            int completedCount = overviewMapper.getMonthlyCompletedCount(month);
            int noShowCount = overviewMapper.getMonthlyNoShowCount(month);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("reservedCount", reservedCount);
            resultMap.put("canceledCount", canceledCount);
            resultMap.put("completedCount", completedCount);
            resultMap.put("noShowCount", noShowCount);

            return RESP.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("查询月份预约统计失败：" + e.getMessage());
        }
    }
}
