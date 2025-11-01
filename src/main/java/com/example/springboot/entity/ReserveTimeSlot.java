package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Time;
import java.util.Date;
@Data
public class ReserveTimeSlot {
    /**
     * 预约时间段唯一标识
     */
    private Long reserveTimeId;

    /**
     * 关联咨询师ID（对应counselor_info表的counselor_id）
     */
    private String counselorId;

    /**
     * 日期（如 2025-10-01）
     */

    private Date reserveDate;

    /**
     * 开始时间（如 08:00:00）
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startTime;

    /**
     * 结束时间（如 09:00:00）
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "Asia/Shanghai")
    private Date endTime;

    /**
     * 是否空闲：0=空闲，1=已被预约
     */
    private Integer isOccupied;

    /**
     * 已预约学生（若为空表示无人预约）
     */
    private String studentId;

    /**
     * 创建时间（系统生成）
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
