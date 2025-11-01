package com.example.springboot.service;

import com.example.springboot.entity.ReserveTimeSlot;
import com.example.springboot.utils.RESP;

import java.util.Date;
import java.util.List;

public interface ReserveTimeSlotService {

    /**
     * 新增排班时间段（自动拆分1小时段）
     */
    RESP addReserveTimeSlot(String counselorId, Date reserveDate, String startTime, String endTime);

    /**
     * 查询指定日期的所有时间段
     */
    RESP listReserveTimeSlots(String reserveDate);

    /**
     * 更新排班时间段（一般用于修改预约状态或学生信息）
     */
    RESP updateReserveTimeSlot(ReserveTimeSlot slot);

    /**
     * 批量删除指定的时间段
     */
    RESP deleteReserveTimeSlots(List<Long> timeIdList);

    /**
     * 分页查询咨询师排班，可模糊搜索工号
     */
    RESP listSlotsByCounselorId(String counselorIdPattern, int pageNum, int pageSize);

    /**
     * 查询当前日期及后7天的排班记录
     */
    RESP listSlotsNext7Days(Date startDate);

}
