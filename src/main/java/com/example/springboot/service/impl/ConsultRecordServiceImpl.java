package com.example.springboot.service.impl;

import com.example.springboot.dto.CounselorUnfilledConsultQueryDTO;
import com.example.springboot.entity.ConsultRecord;
import com.example.springboot.dto.ConsultRecordQueryDTO;
import com.example.springboot.mapper.ConsultRecordMapper;
import com.example.springboot.service.ConsultRecordService;
import com.example.springboot.common.PageResult;
import com.example.springboot.vo.UnfilledConsultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultRecordServiceImpl implements ConsultRecordService {

    @Autowired
    private ConsultRecordMapper consultRecordMapper;

    @Override
    public PageResult<ConsultRecord> getConsultRecordList(ConsultRecordQueryDTO queryDTO) {
        // 1. 计算分页偏移量（offset = (页码-1)*每页条数）
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);

        // 2. 调用Mapper查询列表和总数
        List<ConsultRecord> consultRecordList = consultRecordMapper.selectConsultRecordList(queryDTO);
        int total = consultRecordMapper.countConsultRecord(queryDTO);

        // 3. 构建分页结果（PageResult为通用分页类，含列表、总数、页码等）
        return new PageResult<>(
                total,
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                consultRecordList
                );
    }

    @Override
    public PageResult<UnfilledConsultVO> getUnfilledConsultRecords(CounselorUnfilledConsultQueryDTO queryDTO) {
        // 计算分页偏移量
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);

        // 查询未填写的咨询记录列表
        List<UnfilledConsultVO> recordList = consultRecordMapper.selectUnfilledConsultRecords(queryDTO);
        // 统计总数
        int total = consultRecordMapper.countUnfilledConsultRecords(queryDTO);

        // 构建分页结果
        return new PageResult<>(
                total,
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                recordList
                );
    }
}