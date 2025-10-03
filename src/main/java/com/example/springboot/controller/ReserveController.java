package com.example.springboot.controller;

import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.dto.*;
import com.example.springboot.service.ReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 预约服务控制器
 * 处理预约相关的HTTP请求
 */
@RestController
@RequestMapping("/api/reserve")
public class ReserveController {

    @Autowired
    private ReserveService reserveService;

    /**
     * 根据时段和咨询原因匹配咨询师
     */
    @PostMapping("/match")
    public Result matchCounselor(@RequestBody CounselorMatchQueryDTO queryDTO) {
        return Result.success(reserveService.matchCounselor(queryDTO));
    }

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result createReservation(@RequestBody ReservationAddDTO addDTO) {
        return Result.success(reserveService.createReservation(addDTO));
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result cancelReservation(@RequestBody ReservationCancelParamDTO cancelDTO) {
        return Result.success(reserveService.cancelReservation(cancelDTO));
    }

    /**
     * 学生查询个人预约记录
     */
    @GetMapping("/student/list")
    public Result getStudentReservationList(
            @RequestParam String studentId,
            @RequestParam(required = false) Integer reserveStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reserveService.getStudentReservationList(studentId, reserveStatus, pageNum, pageSize));
    }

    /**
     * 咨询师查询名下预约记录
     */
    @GetMapping("/counselor/list")
    public Result getCounselorReservationList(
            @RequestParam String counselorId,
            @RequestParam(required = false) String reserveDate,
            @RequestParam(required = false) Integer reserveStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reserveService.getCounselorReservationList(counselorId, reserveDate, reserveStatus, pageNum, pageSize));
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/detail/{reserveId}")
    public Result getReservationDetail(@PathVariable Long reserveId) {
        return Result.success(reserveService.getReservationDetail(reserveId));
    }

    /**
     * 标记咨询完成
     */
    @PutMapping("/counselor/markCompleted")
    public Result markReservationCompleted(@RequestBody ReservationStatusOperateDTO operateDTO) {
        return Result.success(reserveService.markReservationCompleted(operateDTO));
    }

    /**
     * 标记学生爽约
     */
    @PutMapping("/counselor/markNoShow")
    public Result markReservationNoShow(@RequestBody ReservationStatusOperateDTO operateDTO) {
        return Result.success(reserveService.markReservationNoShow(operateDTO));
    }

    /**
     * 填写咨询记录
     */
    @PostMapping("/counselor/createConsultRecord")
    public Result createConsultRecord(@RequestBody ConsultRecordAddDTO addDTO) {
        return Result.success(reserveService.createConsultRecord(addDTO));
    }

    /**
     * 为咨询师创建可预约时段（排班）
     */
    @PostMapping("/time/create")
    public Result createReserveTime(@RequestBody ReserveTimeAddDTO addDTO) {
        return Result.success(reserveService.createReserveTime(addDTO));
    }

    /**
     * 批量删除可预约时段（管理员）
     */
    @PostMapping("/time/delete-batch")
    public Result deleteReserveTimeBatch(@RequestBody ReserveTimeDeleteParamDTO deleteDTO) {
        return Result.success(reserveService.deleteReserveTimeBatch(deleteDTO));
    }
}
