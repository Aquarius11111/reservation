package com.example.springboot.service;

import com.example.springboot.common.PageResult;
import com.example.springboot.dto.*;

import java.util.List;

public interface ReserveService {
    /**
     * 获取咨询原因下拉列表
     */
//    List<String> getConsultReasonList();

    /**
     * 获取未来7天空闲时段列表
     */
//    List<DateFreeTimeDTO> getFuture7DaysFreeTime(String currentDate);

    /**
     * 根据时段和咨询原因匹配咨询师
     */
    PageResult<CounselorMatchDTO> matchCounselor(CounselorMatchQueryDTO queryDTO);

    /**
     * 创建预约
     */
    ReservationCreateDTO createReservation(ReservationAddDTO addDTO);

    /**
     * 取消预约
     */
    ReservationCancelDTO cancelReservation(ReservationCancelParamDTO cancelDTO);

    /**
     * 学生查询个人预约记录
     */
    PageResult<StudentReservationDTO> getStudentReservationList(
            String studentId, Integer reserveStatus, int pageNum, int pageSize);

    /**
     * 咨询师查询名下预约记录
     */
    PageResult<CounselorReservationDTO> getCounselorReservationList(
            String counselorId, String reserveDate, Integer reserveStatus, int pageNum, int pageSize);

    /**
     * 获取预约详情
     */
    ReservationDetailDTO getReservationDetail(Long reserveId);

    /**
     * 标记咨询完成
     */
    ReservationStatusDTO markReservationCompleted(ReservationStatusOperateDTO operateDTO);

    /**
     * 标记学生爽约
     */
    ReservationStatusDTO markReservationNoShow(ReservationStatusOperateDTO operateDTO);

    /**
     * 填写咨询记录
     */
    ConsultRecordCreateDTO createConsultRecord(ConsultRecordAddDTO addDTO);

    /**
     * 为咨询师创建可预约时段（排班）
     */
    ReserveTimeCreateDTO createReserveTime(ReserveTimeAddDTO addDTO);

    /**
     * 批量删除可预约时段（管理员）
     */
    ReserveTimeDeleteDTO deleteReserveTimeBatch(ReserveTimeDeleteParamDTO deleteDTO);
}
