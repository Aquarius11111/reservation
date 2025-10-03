package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ConsultRecord {
    /**
     * 咨询记录唯一标识
     */
    private Long recordId;

    /**
     * 关联预约记录ID（对应reservation_info表的reserve_id）
     */
    private Long reserveId;

    /**
     * 关联咨询师ID（对应counselor_info表的counselor_id）
     */
    private String counselorId;

    /**
     * 实际咨询日期（需与预约日期一致或备注说明）
     */
    private Date consultDate;

    /**
     * 学生表现（如 “情绪低落，表达清晰，主动提及压力源”）
     */
    private String studentPerformance;

    /**
     * 咨询核心内容
     */
    private String consultContent;

    /**
     * 后续方案建议
     */
    private String advicePlan;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 记录修改时间
     */
    private Date updateTime;
}
