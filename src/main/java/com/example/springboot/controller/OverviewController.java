package com.example.springboot.controller;

import com.example.springboot.service.OverviewService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/overview")
public class OverviewController {

    @Autowired
    private OverviewService overviewService;

    /** 获取预约数据概览 */
    @GetMapping("/reservation")
    public RESP getReservationOverview() {
        return overviewService.getReservationOverview();
    }
    /** 查询某个咨询师的预约统计与明细 */
    @GetMapping("/counselor/{counselorId}")
    public RESP getCounselorOverview(@PathVariable String counselorId) {
        return overviewService.getCounselorOverview(counselorId);
    }
    /** 获取 SCL-90 测评总览 */
    @GetMapping("/evaluate")
    public RESP getEvaluateOverview() {
        return overviewService.getEvaluateOverview();
    }
    /** 查询单个学生的测评总数和测评记录 */
    @GetMapping("/evaluate/student/{studentId}")
    public RESP getEvaluateByStudent(@PathVariable String studentId) {
        return overviewService.getEvaluateByStudent(studentId);
    }
    /** 按月份统计预约数据 */
    @GetMapping("/reservation/monthly")
    public RESP getMonthlyReservationOverview(@RequestParam String month) {
        return overviewService.getMonthlyReservationOverview(month);
    }
}
