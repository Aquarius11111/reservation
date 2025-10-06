package com.example.springboot.mapper;

import com.example.springboot.dto.ConsultRecordQueryDTO;
import com.example.springboot.entity.ConsultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 查询咨询记录列表（返回实体类列表，含业务扩展字段）
     */
    List<ConsultRecord> selectConsultRecordList(ConsultRecordQueryDTO queryDTO);

    /**
     * 查询咨询记录总数（用于分页计算）
     */
    int countConsultRecord(ConsultRecordQueryDTO queryDTO);
}