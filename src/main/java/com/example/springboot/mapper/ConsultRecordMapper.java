package com.example.springboot.mapper;

import com.example.springboot.entity.ConsultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 咨询记录Mapper
 */
@Mapper
public interface ConsultRecordMapper {

    /**
     * 新增咨询记录
     */
    int insert(ConsultRecord consultRecord);

    /**
     * 根据预约ID查询是否已存在咨询记录
     */
    int countByReserveId(@Param("reserveId") Long reserveId);
}