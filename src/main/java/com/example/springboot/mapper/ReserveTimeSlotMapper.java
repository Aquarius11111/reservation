package com.example.springboot.mapper;

import com.example.springboot.dto.CounselorMatchDetailDTO;
import com.example.springboot.dto.SelectedTimeDTO;
import com.example.springboot.entity.ReserveTimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 预约时段Mapper
 */
@Mapper
public interface ReserveTimeSlotMapper {

    /**
     * 根据日期查询空闲时段
     */
    List<ReserveTimeSlot> selectFreeByDate(@Param("reserveDate") String reserveDate);

    /**
     * 根据ID查询时段
     */
    ReserveTimeSlot selectById(@Param("reserveTimeId") Long reserveTimeId);

    /**
     * 根据日期和开始时间查询时段
     */
    ReserveTimeSlot selectByDateAndTime(
            @Param("reserveDate") java.util.Date reserveDate,
            @Param("startTime") String startTime);

    /**
     * 校验时段是否重叠（排班时用）
     */
    int checkTimeOverlap(
            @Param("counselorId") String counselorId,
            @Param("reserveDate") String reserveDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * 批量查询时段是否有已占用（删除前校验）
     */
    int countOccupiedByIds(@Param("timeIdList") List<Long> timeIdList);

    /**
     * 按咨询师ID和日期查询已占用时段数量（删除前校验）
     */
    int countOccupiedByCounselorAndDate(
            @Param("counselorId") String counselorId,
            @Param("reserveDate") String reserveDate);

    /**
     * 新增时段
     */
    int insert(ReserveTimeSlot reserveTimeSlot);

    /**
     * 更新时段
     */
    int updateById(ReserveTimeSlot reserveTimeSlot);

    /**
     * 批量删除时段
     */
    int deleteBatchIds(@Param("timeIdList") List<Long> timeIdList);

    /**
     * 按咨询师ID和日期删除时段
     */
    int deleteByCounselorAndDate(
            @Param("counselorId") String counselorId,
            @Param("reserveDate") String reserveDate);

    /**
     * 查询匹配的咨询师及时段详情
     * @param consultReason 咨询原因
     * @param selectedTimeList 学生选择的时段列表
     * @param offset 分页偏移量
     * @param pageSize 每页条数
     * @return 匹配详情列表
     */
    List<CounselorMatchDetailDTO> selectMatchedCounselors(
            @Param("consultReason") String consultReason,
            @Param("selectedTimeList") List<SelectedTimeDTO> selectedTimeList,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计匹配的咨询师总数
     * @param consultReason 咨询原因
     * @param selectedTimeList 学生选择的时段列表
     * @return 咨询师总数
     */
    int countMatchedCounselors(
            @Param("consultReason") String consultReason,
            @Param("selectedTimeList") List<SelectedTimeDTO> selectedTimeList);

    ReserveTimeSlot selectByDateAndTimeAndCounselorId(
            @Param("reserveDate") java.util.Date reserveDate,
            @Param("startTime") String startTime,
            @Param("counselorId") String counselorId);

    List<ReserveTimeSlot> selectByCounselorIdLike(
            @Param("counselorId") String counselorId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    int countByCounselorIdLike(@Param("counselorId") String counselorId);


}
