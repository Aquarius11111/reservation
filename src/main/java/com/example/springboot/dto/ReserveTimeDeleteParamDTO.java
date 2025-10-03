package com.example.springboot.dto;

import lombok.Data;

import java.util.List;

// 批量删除可预约时段请求DTO
@Data
public class ReserveTimeDeleteParamDTO {
    private String counselorId;
    private String reserveDate;
    private List<Long> reserveTimeIdList;
}
