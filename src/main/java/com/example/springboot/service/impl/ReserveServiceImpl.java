package com.example.springboot.service.impl;

import com.example.springboot.common.PageResult;
import com.example.springboot.dto.*;
import com.example.springboot.entity.ConsultRecord;
import com.example.springboot.entity.ReservationInfo;
import com.example.springboot.entity.ReserveTimeSlot;
import com.example.springboot.entity.StudentInfo;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.*;
import com.example.springboot.service.ReserveService;
import com.example.springboot.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReserveServiceImpl implements ReserveService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CounselorInfoMapper counselorInfoMapper;

    @Autowired
    private ReserveTimeSlotMapper reserveTimeSlotMapper;

    @Autowired
    private ReservationInfoMapper reservationInfoMapper;

    @Autowired
    private ConsultRecordMapper consultRecordMapper;

    /**
     * 获取咨询原因下拉列表（从咨询师专长中提取并去重）
     */
//    @Override
//    public List<String> getConsultReasonList() {
//        // 查询所有咨询师的专长
//        List<String> specialties = counselorInfoMapper.selectAllSpecialties();
//        Set<String> reasonSet = new HashSet<>();
//
//        // 拆分专长字符串并去重
//        if (!CollectionUtils.isEmpty(specialties)) {
//            for (String specialty : specialties) {
//                if (StringUtils.hasText(specialty)) {
//                    String[] reasons = specialty.split(",");
//                    for (String reason : reasons) {
//                        if (StringUtils.hasText(reason)) {
//                            reasonSet.add(reason.trim());
//                        }
//                    }
//                }
//            }
//        }
//
//        // 转换为有序列表并返回
//        return new ArrayList<>(reasonSet);
//    }

    /**
     * 获取未来7天空闲时段列表
     */
//    @Override
//    public List<DateFreeTimeDTO> getFuture7DaysFreeTime(String currentDate) {
//        // 确定起始日期（默认当前日期）
//        Date startDate = StringUtils.hasText(currentDate)
//                ? DateUtils.parseDate(currentDate)
//                : new Date();
//
//        List<DateFreeTimeDTO> resultList = new ArrayList<>();
//
//        // 循环获取未来7天的时段
//        for (int i = 0; i < 7; i++) {
//            Date current = DateUtils.addDays(startDate, i);
//            String dateStr = DateUtils.formatDate(current);
//
//            // 查询该日期下所有空闲时段
//            List<ReserveTimeSlot> slots = reserveTimeSlotMapper.selectFreeByDate(dateStr);
//
//            if (!CollectionUtils.isEmpty(slots)) {
//                DateFreeTimeDTO dateFreeTime = new DateFreeTimeDTO();
//                dateFreeTime.setReserveDate(dateStr);
//
//                // 转换为DTO
//                List<ReserveTimeDTO> timeList = slots.stream().map(slot -> {
//                    ReserveTimeDTO timeDTO = new ReserveTimeDTO();
//                    timeDTO.setReserveTimeId(slot.getReserveTimeId());
//                    timeDTO.setReserveDate(dateStr);
//                    timeDTO.setStartTime(DateUtils.formatTime(slot.getStartTime()));
//                    timeDTO.setEndTime(DateUtils.formatTime(slot.getEndTime()));
//                    timeDTO.setIsOccupied(slot.getIsOccupied());
//                    return timeDTO;
//                }).collect(Collectors.toList());
//
//                dateFreeTime.setTimeList(timeList);
//                resultList.add(dateFreeTime);
//            }
//        }
//
//        return resultList;
//    }

    /**
     * 根据时段和咨询原因匹配咨询师
     */
    @Override
    public PageResult<CounselorMatchDTO> matchCounselor(CounselorMatchQueryDTO queryDTO) {
        // 1. 验证学生选择的时段是否在未来7天内
        validateTimeRange(queryDTO.getSelectedTimeList());

        // 2. 计算分页偏移量
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

        // 3. 查询匹配的咨询师及时段详情（带分页）
        List<CounselorMatchDetailDTO> detailList = reserveTimeSlotMapper.selectMatchedCounselors(
                queryDTO.getConsultReason(),
                queryDTO.getSelectedTimeList(),
                offset,
                queryDTO.getPageSize());

        // 4. 统计总条数（去重咨询师ID）
        int total = reserveTimeSlotMapper.countMatchedCounselors(
                queryDTO.getConsultReason(),
                queryDTO.getSelectedTimeList());

        // 5. 按咨询师ID分组，整合时段信息（包含真实ID）
        Map<String, CounselorMatchDTO> counselorMap = new HashMap<>();
        for (CounselorMatchDetailDTO detail : detailList) {
            String counselorId = detail.getCounselorId();

            // 首次处理该咨询师
            if (!counselorMap.containsKey(counselorId)) {
                CounselorMatchDTO matchDTO = new CounselorMatchDTO();
                BeanUtils.copyProperties(detail, matchDTO);
                matchDTO.setMatchedTimeList(new ArrayList<>());
                counselorMap.put(counselorId, matchDTO);
            }

            // 添加包含真实ID的时段信息
            CounselorMatchDTO matchDTO = counselorMap.get(counselorId);
            ReserveTimeDTO timeDTO = new ReserveTimeDTO();
            BeanUtils.copyProperties(detail, timeDTO); // 复制reserveTimeId等时段字段
            matchDTO.getMatchedTimeList().add(timeDTO);
        }

        // 6. 转换为分页结果
        List<CounselorMatchDTO> resultList = new ArrayList<>(counselorMap.values());
        return new PageResult<>(total, queryDTO.getPageNum(), queryDTO.getPageSize(), resultList);
    }

    // 验证所选时段是否在未来7天内
    private void validateTimeRange(List<SelectedTimeDTO> timeList) {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        for (SelectedTimeDTO time : timeList) {
            try {
                LocalDate selectDate = LocalDate.parse(time.getReserveDate());
                if (selectDate.isBefore(today) || selectDate.isAfter(sevenDaysLater)) {
                    throw new ServiceException("选择的时段必须在未来7天内");
                }
            } catch (DateTimeParseException e) {
                throw new ServiceException("日期格式错误，正确格式为yyyy-MM-dd");
            }
        }
    }

    /**
     * 创建预约（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReservationCreateDTO createReservation(ReservationAddDTO addDTO) {
        // 1. 校验时段是否空闲
        ReserveTimeSlot timeSlot = reserveTimeSlotMapper.selectById(addDTO.getReserveTimeId());
        if (timeSlot == null || timeSlot.getIsOccupied() == 1) {
            throw new ServiceException("该时段已被预约，请选择其他时段");
        }

        // 2. 校验学生是否已完成首次测评（简化逻辑）
        // StudentEvaluateResult result = evaluateMapper.selectFirstByStudentId(addDTO.getStudentId());
        // if (result == null) {
        //     throw new ServiceException("请先完成首次心理测评");
        // }

        // 3. 创建预约记录
        ReservationInfo reservation = new ReservationInfo();
        BeanUtils.copyProperties(addDTO, reservation);
        reservation.setReserveDate(timeSlot.getReserveDate());
        reservation.setReserveTime(
                DateUtils.formatTime(timeSlot.getStartTime()) + "-" +
                        DateUtils.formatTime(timeSlot.getEndTime())
        );
        reservation.setReserveStatus(1); // 1=已预约
        reservation.setConsultRoom(counselorInfoMapper.selectById(reservation.getCounselorId()).getLocation());
        reservationInfoMapper.insert(reservation);

        // 4. 更新时段状态为已占用
        timeSlot.setIsOccupied(1);
        timeSlot.setStudentId(addDTO.getStudentId());
        reserveTimeSlotMapper.updateById(timeSlot);

        // 5. 组装返回结果
        ReservationCreateDTO result = new ReservationCreateDTO();
        result.setReserveId(reservation.getReserveId());
        result.setReserveDate(DateUtils.formatDate(reservation.getReserveDate()));
        result.setReserveTime(reservation.getReserveTime());
        result.setCounselorName(sysUserMapper.selectById(addDTO.getCounselorId()).getUserName());
        result.setConsultRoom(reservation.getConsultRoom());
        result.setReserveStatus(reservation.getReserveStatus());
        result.setCreateTime(DateUtils.formatDateTime(new Date()));

        return result;
    }

    /**
     * 取消预约（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReservationCancelDTO cancelReservation(ReservationCancelParamDTO cancelDTO) {
        // 1. 查询预约记录
        ReservationInfo reservation = reservationInfoMapper.selectById(cancelDTO.getReserveId());
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }

        // 2. 校验状态（只能取消"已预约"状态的记录）
        if (reservation.getReserveStatus() != 1) {
            throw new ServiceException("只能取消状态为'已预约'的记录");
        }

        // 3. 校验取消时间（需在咨询开始前1小时）
        Date consultDate = DateUtils.parseDate(DateUtils.formatDate(reservation.getReserveDate()));
        String[] timeParts = reservation.getReserveTime().split("-");
        if (timeParts.length != 2) {
            throw new ServiceException("预约时间格式错误");
        }

        Date consultStartTime = DateUtils.parseDateTime(
                DateUtils.formatDate(consultDate) + " " + timeParts[0]
        );
        Date cancelDeadline = DateUtils.addHours(consultStartTime, -1); // 提前1小时

        if (new Date().after(cancelDeadline)) {
            throw new ServiceException("只能在咨询开始前1小时取消预约");
        }

        // 4. 更新预约状态
        reservation.setReserveStatus(2); // 2=已取消
        reservation.setCancelReason(cancelDTO.getCancelReason());
        reservationInfoMapper.updateById(reservation);

        // 5. 更新时段状态为空闲
        ReserveTimeSlot timeSlot = reserveTimeSlotMapper.selectByDateAndTimeAndCounselorId(
                reservation.getReserveDate(),
                timeParts[0],
                reservation.getCounselorId()
        );
        if (timeSlot != null) {
            timeSlot.setIsOccupied(0);
            timeSlot.setStudentId(null);
            reserveTimeSlotMapper.updateById(timeSlot);
        }

        // 6. 组装返回结果
        ReservationCancelDTO result = new ReservationCancelDTO();
        result.setReserveId(reservation.getReserveId());
        result.setReserveStatus(reservation.getReserveStatus());
        result.setUpdateTime(DateUtils.formatDateTime(reservation.getUpdateTime()));

        return result;
    }

    /**
     * 学生查询个人预约记录
     */
    @Override
    public PageResult<StudentReservationDTO> getStudentReservationList(
            String studentId, Integer reserveStatus, int pageNum, int pageSize) {

        int offset = (pageNum - 1) * pageSize;
        List<StudentReservationDTO> list = reservationInfoMapper.selectByStudent(
                studentId, reserveStatus, offset, pageSize);

        int total = reservationInfoMapper.countByStudent(studentId, reserveStatus);

        return new PageResult<>(total, pageNum, pageSize, list);
    }

    /**
     * 咨询师查询名下预约记录
     */
    @Override
    public PageResult<CounselorReservationDTO> getCounselorReservationList(
            String counselorId, String reserveDate, Integer reserveStatus, int pageNum, int pageSize) {

        int offset = (pageNum - 1) * pageSize;
        List<CounselorReservationDTO> list = reservationInfoMapper.selectByCounselor(
                counselorId, reserveDate, reserveStatus, offset, pageSize);

        int total = reservationInfoMapper.countByCounselor(counselorId, reserveDate, reserveStatus);

        return new PageResult<>(total, pageNum, pageSize, list);
    }

    /**
     * 获取预约详情
     */
    @Override
    public ReservationDetailDTO getReservationDetail(Long reserveId) {
        ReservationDetailDTO detail = reservationInfoMapper.selectDetailById(reserveId);
        if (detail == null) {
            throw new ServiceException("预约记录不存在");
        }
        return detail;
    }

    /**
     * 标记咨询完成（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReservationStatusDTO markReservationCompleted(ReservationStatusOperateDTO operateDTO) {
        // 1. 查询预约记录
        ReservationInfo reservation = reservationInfoMapper.selectById(operateDTO.getReserveId());
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }

        // 2. 校验权限（只能操作自己的预约）
        if (!reservation.getCounselorId().equals(operateDTO.getCounselorId())) {
            throw new ServiceException("无权操作该预约记录");
        }

        // 3. 校验状态（只能标记"已预约"的记录）
        if (reservation.getReserveStatus() != 1) {
            throw new ServiceException("只能标记状态为'已预约'的记录为完成");
        }

        // 4. 校验日期（只能标记当天的预约）
//        String today = DateUtils.formatDate(new Date());
//        if (!today.equals(DateUtils.formatDate(reservation.getReserveDate()))) {
//            throw new ServiceException("只能标记当天的预约为完成");
//        }

        // 5. 更新状态
        reservation.setReserveStatus(3); // 3=已完成
        reservationInfoMapper.updateById(reservation);

        // 6. 组装返回结果
        ReservationStatusDTO result = new ReservationStatusDTO();
        result.setReserveId(reservation.getReserveId());
        result.setReserveStatus(reservation.getReserveStatus());
        result.setUpdateTime(DateUtils.formatDateTime(reservation.getUpdateTime()));

        return result;
    }

    /**
     * 标记学生爽约（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReservationStatusDTO markReservationNoShow(ReservationStatusOperateDTO operateDTO) {
        // 1. 查询预约记录
        ReservationInfo reservation = reservationInfoMapper.selectById(operateDTO.getReserveId());
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }

        // 2. 校验权限
        if (!reservation.getCounselorId().equals(operateDTO.getCounselorId())) {
            throw new ServiceException("无权操作该预约记录");
        }

        // 3. 校验状态
        if (reservation.getReserveStatus() != 1) {
            throw new ServiceException("只能标记状态为'已预约'的记录为爽约");
        }

        // 4. 校验时间（需在咨询结束后）
        Date consultDate = reservation.getReserveDate();
        String[] timeParts = reservation.getReserveTime().split("-");
        if (timeParts.length != 2) {
            throw new ServiceException("预约时间格式错误");
        }

        Date consultEndTime = DateUtils.parseDateTime(
                DateUtils.formatDate(consultDate) + " " + timeParts[1]
        );

        if (new Date().before(consultEndTime)) {
            throw new ServiceException("需在咨询结束后才能标记爽约");
        }

        // 5. 更新状态
        reservation.setReserveStatus(4); // 4=已爽约
        reservationInfoMapper.updateById(reservation);

        // 6. 组装返回结果
        ReservationStatusDTO result = new ReservationStatusDTO();
        result.setReserveId(reservation.getReserveId());
        result.setReserveStatus(reservation.getReserveStatus());
        result.setUpdateTime(DateUtils.formatDateTime(reservation.getUpdateTime()));

        return result;
    }

    /**
     * 填写咨询记录（带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ConsultRecordCreateDTO createConsultRecord(ConsultRecordAddDTO addDTO) {
        // 1. 校验预约记录是否存在且已完成
        ReservationInfo reservation = reservationInfoMapper.selectById(addDTO.getReserveId());
        if (reservation == null) {
            throw new ServiceException("预约记录不存在");
        }

        if (reservation.getReserveStatus() != 3) {
            throw new ServiceException("只能为'已完成'的预约填写咨询记录");
        }

        // 2. 校验权限
        if (!reservation.getCounselorId().equals(addDTO.getCounselorId())) {
            throw new ServiceException("无权为该预约填写咨询记录");
        }

        // 3. 校验是否已存在记录
        if (consultRecordMapper.countByReserveId(addDTO.getReserveId()) > 0) {
            throw new ServiceException("该预约已存在咨询记录，不可重复添加");
        }

        // 4. 创建咨询记录
        ConsultRecord record = new ConsultRecord();
        BeanUtils.copyProperties(addDTO, record);
        record.setConsultDate(DateUtils.parseDate(addDTO.getConsultDate()));
        consultRecordMapper.insert(record);

        // 5. 组装返回结果
        ConsultRecordCreateDTO result = new ConsultRecordCreateDTO();
        result.setRecordId(record.getRecordId());
        result.setReserveId(record.getReserveId());
        result.setCreateTime(DateUtils.formatDateTime(new Date()));

        return result;
    }

    /**
     * 为咨询师创建可预约时段（排班，带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReserveTimeCreateDTO createReserveTime(ReserveTimeAddDTO addDTO) {
        int createdCount = 0;

        // 循环处理每个日期的时段
        for (ReserveDateTimeDTO dateDTO : addDTO.getReserveDateList()) {
            String reserveDate = dateDTO.getReserveDate();

            // 循环处理每个时段
            for (TimeSlotDTO timeSlot : dateDTO.getTimeList()) {
                // 校验时段是否重叠
                if (reserveTimeSlotMapper.checkTimeOverlap(
                        addDTO.getCounselorId(),
                        reserveDate,
                        timeSlot.getStartTime(),
                        timeSlot.getEndTime()
                ) > 0) {
                    throw new ServiceException("时段重叠：" + reserveDate + " " +
                            timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
                }

                // 创建时段记录
                ReserveTimeSlot slot = new ReserveTimeSlot();
                slot.setCounselorId(addDTO.getCounselorId());
                slot.setReserveDate(DateUtils.parseDate(reserveDate));
                slot.setStartTime(DateUtils.parseTime(timeSlot.getStartTime()));
                slot.setEndTime(DateUtils.parseTime(timeSlot.getEndTime()));
                slot.setIsOccupied(0);
                reserveTimeSlotMapper.insert(slot);

                createdCount++;
            }
        }

        // 组装返回结果
        ReserveTimeCreateDTO result = new ReserveTimeCreateDTO();
        result.setCreatedTimeCount(createdCount);
        result.setCounselorId(addDTO.getCounselorId());

        return result;
    }

    /**
     * 批量删除可预约时段（管理员，带事务）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReserveTimeDeleteDTO deleteReserveTimeBatch(ReserveTimeDeleteParamDTO deleteDTO) {
        // 优先按ID列表删除
        if (!CollectionUtils.isEmpty(deleteDTO.getReserveTimeIdList())) {
            // 校验是否有已被预约的时段
            int occupiedCount = reserveTimeSlotMapper.countOccupiedByIds(deleteDTO.getReserveTimeIdList());
            if (occupiedCount > 0) {
                throw new ServiceException("包含已被预约的时段，无法删除");
            }

            // 执行删除
            int deleted = reserveTimeSlotMapper.deleteBatchIds(deleteDTO.getReserveTimeIdList());

            ReserveTimeDeleteDTO result = new ReserveTimeDeleteDTO();
            result.setDeletedTimeCount(deleted);
            return result;
        }

        // 按咨询师ID和日期删除
        if (StringUtils.hasText(deleteDTO.getCounselorId()) && StringUtils.hasText(deleteDTO.getReserveDate())) {
            // 校验是否有已被预约的时段
            int occupiedCount = reserveTimeSlotMapper.countOccupiedByCounselorAndDate(
                    deleteDTO.getCounselorId(),
                    deleteDTO.getReserveDate()
            );
            if (occupiedCount > 0) {
                throw new ServiceException("包含已被预约的时段，无法删除");
            }

            // 执行删除
            int deleted = reserveTimeSlotMapper.deleteByCounselorAndDate(
                    deleteDTO.getCounselorId(),
                    deleteDTO.getReserveDate()
            );

            ReserveTimeDeleteDTO result = new ReserveTimeDeleteDTO();
            result.setDeletedTimeCount(deleted);
            return result;
        }

        throw new ServiceException("请至少指定时段ID列表或咨询师ID+日期");
    }
}
