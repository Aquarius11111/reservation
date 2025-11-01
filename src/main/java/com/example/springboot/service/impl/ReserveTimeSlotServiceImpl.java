package com.example.springboot.service.impl;

import com.example.springboot.entity.ReserveTimeSlot;
import com.example.springboot.mapper.ReserveTimeSlotMapper;
import com.example.springboot.service.ReserveTimeSlotService;
import com.example.springboot.utils.RESP;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 预约时段业务逻辑实现（管理员排班）
 */
@Service
public class ReserveTimeSlotServiceImpl implements ReserveTimeSlotService {

    @Autowired
    private ReserveTimeSlotMapper reserveTimeSlotMapper;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    // 有效时间段定义
    private static final int MORNING_START = 8;
    private static final int MORNING_END = 12;
    private static final int AFTERNOON_START = 14;
    private static final int AFTERNOON_END = 17;

    @Override
    public RESP addReserveTimeSlot(String counselorId, Date reserveDate, String startTime, String endTime) {
        try {
            // 1. 将 Date 转 LocalDate
            LocalDate date = reserveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 2. 解析开始和结束时间
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            // 3. 校验整点和时间顺序
            if (start.getMinute() != 0 || end.getMinute() != 0 || !start.isBefore(end)) {
                return RESP.error("请选择整点时间，并且结束时间必须大于开始时间。");
            }

            // 4. 校验有效时间段
            if (!isWithinValidRange(start, end)) {
                return RESP.error("请选择有效时间段：每天早上08:00-12:00, 下午14:00-17:00（整点一小时）");
            }

            // 5. 检查冲突时间段
            List<String> conflictSlots = new ArrayList<>();
            List<ReserveTimeSlot> slotsToInsert = new ArrayList<>();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // ✅ 格式化时间

            for (LocalTime t = start; t.isBefore(end); t = t.plusHours(1)) {
                String tStartStr = t.format(timeFormatter);         // "09:00:00"
                String tEndStr = t.plusHours(1).format(timeFormatter); // "10:00:00"

                int overlap = reserveTimeSlotMapper.checkTimeOverlap(
                        counselorId,
                        date.toString(),  // "yyyy-MM-dd"
                        tStartStr,
                        tEndStr
                );
                if (overlap > 0) {
                    conflictSlots.add(t + "-" + t.plusHours(1));
                } else {
                    ReserveTimeSlot slot = new ReserveTimeSlot();
                    slot.setCounselorId(counselorId);
                    slot.setReserveDate(java.sql.Date.valueOf(date));
                    slot.setStartTime(java.sql.Time.valueOf(t));
                    slot.setEndTime(java.sql.Time.valueOf(t.plusHours(1)));
                    slot.setIsOccupied(0);
                    slotsToInsert.add(slot);
                }
            }

            // 6. 如果存在冲突，返回失败信息
            if (!conflictSlots.isEmpty()) {
                return RESP.error("排班失败，以下时间段已存在或重叠：" + String.join(", ", conflictSlots));
            }

            // 7. 插入数据库
            for (ReserveTimeSlot slot : slotsToInsert) {
                reserveTimeSlotMapper.insert(slot);
            }

            // 8. 直接使用 slotsToInsert 返回结果
            List<Map<String, Object>> slotInfos = new ArrayList<>();
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (ReserveTimeSlot slot : slotsToInsert) {
                Map<String, Object> map = new HashMap<>();
                map.put("reserveTimeId", slot.getReserveTimeId());
                map.put("counselorId", slot.getCounselorId());
                map.put("reserveDate", dateFmt.format(slot.getReserveDate()));
                map.put("startTime", timeFmt.format(slot.getStartTime()));
                map.put("endTime", timeFmt.format(slot.getEndTime()));
                map.put("isOccupied", slot.getIsOccupied());
                map.put("studentId", slot.getStudentId());
                map.put("createdAt", slot.getCreatedAt() != null ? dateTimeFmt.format(slot.getCreatedAt()) : null);
                map.put("updatedAt", slot.getUpdatedAt() != null ? dateTimeFmt.format(slot.getUpdatedAt()) : null);
                slotInfos.add(map);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "排班成功，生成 " + slotsToInsert.size() + " 个小时段");
            result.put("slots", slotInfos);
            result.put("total", slotInfos.size());

            return RESP.ok(result);

        } catch (Exception e) {
            return RESP.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 判断时间段是否在有效范围内
     */
    private boolean isWithinValidRange(LocalTime start, LocalTime end) {
        // 上午 08:00-12:00
        boolean inMorning = !start.isBefore(LocalTime.of(8, 0)) && !end.isAfter(LocalTime.of(12, 0));
        // 下午 14:00-17:00
        boolean inAfternoon = !start.isBefore(LocalTime.of(14, 0)) && !end.isAfter(LocalTime.of(17, 0));
        return inMorning || inAfternoon;
    }


    /**
     * 拆分为每小时的区间
     */
    private List<String[]> splitIntoOneHourSegments(int startHour, int endHour) {
        List<String[]> segments = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            segments.add(new String[]{String.format("%02d", hour), String.format("%02d", hour + 1)});
        }
        return segments;
    }

    @Override
    public RESP listReserveTimeSlots(String reserveDate) {
        List<ReserveTimeSlot> list = reserveTimeSlotMapper.selectFreeByDate(reserveDate);

        // 准备返回给前端的 List<Map>
        List<Map<String, Object>> resultList = new ArrayList<>();
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");

        for (ReserveTimeSlot slot : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("reserveTimeId", slot.getReserveTimeId());
            map.put("counselorId", slot.getCounselorId());
            map.put("reserveDate", slot.getReserveDate() != null ? dateFmt.format(slot.getReserveDate()) : null);
            map.put("startTime", slot.getStartTime() != null ? timeFmt.format(slot.getStartTime()) : null);
            map.put("endTime", slot.getEndTime() != null ? timeFmt.format(slot.getEndTime()) : null);
            map.put("isOccupied", slot.getIsOccupied());
            map.put("studentId", slot.getStudentId());
            map.put("createdAt", slot.getCreatedAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(slot.getCreatedAt()) : null);
            map.put("updatedAt", slot.getUpdatedAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(slot.getUpdatedAt()) : null);
            resultList.add(map);
        }

        return RESP.ok(resultList);
    }


    @Override
    public RESP updateReserveTimeSlot(ReserveTimeSlot slot) {
        try {
            System.out.println("当前要更新的ID: " + slot.getReserveTimeId());

            slot.setUpdatedAt(new Date());

            if (slot.getReserveDate() == null) {
                return RESP.error("预约日期不能为空");
            }

            // ==================== 处理 startTime / endTime 字符串 ====================
            // 如果前端直接传的 startTime / endTime 是字符串形式的 "HH:mm:ss"，但实体类是 Date
            // 可尝试先把 Date 转 LocalTime，如果 null 就用字符串解析
            LocalTime start, end;
            try {
                if (slot.getStartTime() != null && slot.getEndTime() != null) {
                    start = slot.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    end = slot.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                } else {
                    // 从 JSON 里获取字符串
                    String startStr = slot.getStartTime() != null ? new SimpleDateFormat("HH:mm:ss").format(slot.getStartTime()) : null;
                    String endStr = slot.getEndTime() != null ? new SimpleDateFormat("HH:mm:ss").format(slot.getEndTime()) : null;

                    if (startStr == null || endStr == null) {
                        return RESP.error("开始时间或结束时间不能为空");
                    }

                    start = LocalTime.parse(startStr);
                    end = LocalTime.parse(endStr);

                    slot.setStartTime(java.sql.Time.valueOf(start));
                    slot.setEndTime(java.sql.Time.valueOf(end));
                }
            } catch (Exception e) {
                return RESP.error("时间格式错误：" + e.getMessage());
            }
            // ========================================================================

            // 校验整点和时间顺序
            if (start.getMinute() != 0 || end.getMinute() != 0 || !start.isBefore(end)) {
                return RESP.error("请选择整点时间，并且结束时间必须大于开始时间。");
            }

            // 校验有效时间段
            if (!isWithinValidRange(start, end)) {
                return RESP.error("请选择有效时间段：每天早上08:00-12:00, 下午14:00-17:00（整点一小时）");
            }

            // 检查冲突时间段（按每小时拆分）
            LocalDate date = slot.getReserveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            List<String> conflictSlots = new ArrayList<>();
            List<ReserveTimeSlot> slotsToUpdate = new ArrayList<>();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (LocalTime t = start; t.isBefore(end); t = t.plusHours(1)) {
                String tStartStr = t.format(timeFormatter);
                String tEndStr = t.plusHours(1).format(timeFormatter);

                int overlap = reserveTimeSlotMapper.checkTimeOverlap(
                        slot.getCounselorId(),
                        date.toString(),
                        tStartStr,
                        tEndStr
                );
                if (overlap > 0) {
                    conflictSlots.add(tStartStr + "-" + tEndStr);
                } else {
                    ReserveTimeSlot tempSlot = new ReserveTimeSlot();
                    tempSlot.setReserveTimeId(slot.getReserveTimeId());
                    tempSlot.setCounselorId(slot.getCounselorId());
                    tempSlot.setReserveDate(java.sql.Date.valueOf(date));
                    tempSlot.setStartTime(java.sql.Time.valueOf(t));
                    tempSlot.setEndTime(java.sql.Time.valueOf(t.plusHours(1)));
                    tempSlot.setIsOccupied(slot.getIsOccupied());
                    tempSlot.setStudentId(slot.getStudentId());
                    tempSlot.setUpdatedAt(slot.getUpdatedAt());
                    slotsToUpdate.add(tempSlot);
                }
            }

            if (!conflictSlots.isEmpty()) {
                return RESP.error("更新失败，以下时间段已存在或重叠：" + String.join(", ", conflictSlots));
            }

            // 更新数据库
            int result = 0;
            for (ReserveTimeSlot s : slotsToUpdate) {
                result += reserveTimeSlotMapper.updateById(s);
            }
            if (result <= 0) {
                return RESP.error("更新失败，记录不存在");
            }

            // 构造返回结果
            List<Map<String, Object>> slotInfos = new ArrayList<>();
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (ReserveTimeSlot s : slotsToUpdate) {
                Map<String, Object> map = new HashMap<>();
                map.put("reserveTimeId", s.getReserveTimeId());
                map.put("counselorId", s.getCounselorId());
                map.put("reserveDate", dateFmt.format(s.getReserveDate()));
                map.put("startTime", timeFmt.format(s.getStartTime()));
                map.put("endTime", timeFmt.format(s.getEndTime()));
                map.put("isOccupied", s.getIsOccupied());
                map.put("studentId", s.getStudentId());
                map.put("updatedAt", dateTimeFmt.format(s.getUpdatedAt()));
                slotInfos.add(map);
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("message", "更新成功，生成 " + slotsToUpdate.size() + " 个小时段");
            resultMap.put("slots", slotInfos);
            resultMap.put("total", slotInfos.size());

            return RESP.ok(resultMap);

        } catch (Exception e) {
            return RESP.error("系统错误：" + e.getMessage());
        }
    }



    @Override
    public RESP listSlotsByCounselorId(String counselorIdPattern, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<ReserveTimeSlot> slotList = reserveTimeSlotMapper.selectByCounselorIdLike(counselorIdPattern, offset, pageSize);
            int total = reserveTimeSlotMapper.countByCounselorIdLike(counselorIdPattern);
            return RESP.ok(slotList, pageNum, total);
        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("分页查询咨询师排班失败: " + e.getMessage());
        }
    }




    @Override
    public RESP deleteReserveTimeSlots(List<Long> timeIdList) {
        if (timeIdList == null || timeIdList.isEmpty()) {
            return RESP.error("删除失败：未选择任何记录");
        }

        int occupiedCount = reserveTimeSlotMapper.countOccupiedByIds(timeIdList);
        if (occupiedCount > 0) {
            return RESP.error("删除失败：部分时间段已被预约，无法删除");
        }

        int deleted = reserveTimeSlotMapper.deleteBatchIds(timeIdList);
        return RESP.ok("成功删除 " + deleted + " 条排班记录");
    }

    @Override
    public RESP listSlotsNext7Days(Date startDate) {
        try {
            // 1. 计算后7天的日期
            LocalDate startLocal = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocal = startLocal.plusDays(7);

            Date endDate = Date.from(endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 2. 查询数据库
            List<ReserveTimeSlot> slots = reserveTimeSlotMapper.selectSlotsWithin7Days(startDate, endDate);

            // 3. 封装结果
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
            List<Map<String, Object>> resultList = new ArrayList<>();

            for (ReserveTimeSlot slot : slots) {
                Map<String, Object> map = new HashMap<>();
                map.put("reserveTimeId", slot.getReserveTimeId());
                map.put("counselorId", slot.getCounselorId());
                map.put("reserveDate", slot.getReserveDate() != null ? dateFmt.format(slot.getReserveDate()) : null);
                map.put("startTime", slot.getStartTime() != null ? timeFmt.format(slot.getStartTime()) : null);
                map.put("endTime", slot.getEndTime() != null ? timeFmt.format(slot.getEndTime()) : null);
                map.put("isOccupied", slot.getIsOccupied());
                map.put("studentId", slot.getStudentId());
                resultList.add(map);
            }

            Map<String, Object> respData = new HashMap<>();
            respData.put("startDate", dateFmt.format(startDate));
            respData.put("endDate", dateFmt.format(endDate));
            respData.put("total", resultList.size());
            respData.put("slots", resultList);

            return RESP.ok(respData);

        } catch (Exception e) {
            e.printStackTrace();
            return RESP.error("查询未来7天排班失败：" + e.getMessage());
        }
    }

}
