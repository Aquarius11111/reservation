package com.example.springboot.controller;

import com.example.springboot.entity.ReserveTimeSlot;
import com.example.springboot.service.ReserveTimeSlotService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;

/**
 * 预约时段控制器（管理员排班）
 */
@RestController
@RequestMapping("/reserveTimeSlot")
public class ReserveTimeSlotController {

    @Autowired
    private ReserveTimeSlotService reserveTimeSlotService;

    /**
     * 新增排班（支持自动拆分1小时段）
     */
    @PostMapping("/add")
    public RESP addSlot(
            @RequestParam String counselorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date reserveDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return reserveTimeSlotService.addReserveTimeSlot(counselorId, reserveDate, startTime, endTime);
    }


    /**
     * 查询某天的所有时段
     */
    @GetMapping("/list")
    public RESP listSlots(@RequestParam String reserveDate) {
        return reserveTimeSlotService.listReserveTimeSlots(reserveDate);
    }

    /**
     * 更新时段（修改预约状态或学生信息）
     */
    @PutMapping("/update")
    public RESP updateSlot(@RequestBody ReserveTimeSlot slot) {
        return reserveTimeSlotService.updateReserveTimeSlot(slot);
    }

    /**
     * 批量删除时段
     */
    @DeleteMapping("/delete")
    public RESP deleteSlots(@RequestBody List<Long> timeIdList) {
        return reserveTimeSlotService.deleteReserveTimeSlots(timeIdList);
    }

    /**
     * 分页查询咨询师排班，支持工号模糊搜索
     */
    @GetMapping("/listByCounselor")
    public RESP listByCounselor(
            @RequestParam(required = false, defaultValue = "") String counselorId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return reserveTimeSlotService.listSlotsByCounselorId(counselorId, pageNum, pageSize);
    }
 /**
     * 查询当前日期及后7天的排班记录
     */
    @GetMapping("/listNext7Days")
    public RESP listNext7Days(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentDate) {
        return reserveTimeSlotService.listSlotsNext7Days(currentDate);
    }

}

