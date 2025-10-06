package com.example.springboot.service;

import com.example.springboot.entity.ConsultRecord;
import com.example.springboot.dto.ConsultRecordQueryDTO;
import com.example.springboot.common.PageResult;

public interface ConsultRecordService {
    /**
     * 获取咨询记录列表（分页）
     * @param queryDTO 查询条件（含分页参数）
     * @return 分页结果（含实体类列表+分页信息）
     */
    PageResult<ConsultRecord> getConsultRecordList(ConsultRecordQueryDTO queryDTO);
}