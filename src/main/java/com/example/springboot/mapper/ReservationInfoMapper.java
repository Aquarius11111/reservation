package com.example.springboot.mapper;

import com.example.springboot.dto.CounselorReservationDTO;
import com.example.springboot.dto.ReservationDetailDTO;
import com.example.springboot.dto.StudentReservationDTO;
import com.example.springboot.entity.ReservationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约信息Mapper
 */
@Mapper
public interface ReservationInfoMapper {

    /**
     * 新增预约记录
     */
    int insert(ReservationInfo reservationInfo);

    /**
     * 根据ID查询预约记录
     */
    ReservationInfo selectById(@Param("reserveId") Long reserveId);

    /**
     * 更新预约记录
     */
    int updateById(ReservationInfo reservationInfo);

    /**
     * 学生查询个人预约记录（带分页）
     */
    List<StudentReservationDTO> selectByStudent(
            @Param("studentId") String studentId,
            @Param("reserveStatus") Integer reserveStatus,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 学生查询个人预约记录总数
     */
    int countByStudent(
            @Param("studentId") String studentId,
            @Param("reserveStatus") Integer reserveStatus);

    /**
     * 咨询师查询名下预约记录（带分页）
     */
    List<CounselorReservationDTO> selectByCounselor(
            @Param("counselorId") String counselorId,
            @Param("reserveDate") String reserveDate,
            @Param("reserveStatus") Integer reserveStatus,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 咨询师查询名下预约记录总数
     */
    int countByCounselor(
            @Param("counselorId") String counselorId,
            @Param("reserveDate") String reserveDate,
            @Param("reserveStatus") Integer reserveStatus);

    /**
     * 查询预约详情（关联咨询师信息）
     */
    ReservationDetailDTO selectDetailById(@Param("reserveId") Long reserveId);
}