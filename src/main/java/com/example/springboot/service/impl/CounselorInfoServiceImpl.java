package com.example.springboot.service.impl;

import com.example.springboot.entity.CounselorInfo;
import com.example.springboot.mapper.CounselorInfoMapper;
import com.example.springboot.service.CounselorInfoService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounselorInfoServiceImpl implements CounselorInfoService {

    @Autowired
    private CounselorInfoMapper counselorInfoMapper;

    @Override
    public RESP addCounselor(CounselorInfo counselor) {
        int result = counselorInfoMapper.insert(counselor);
        return result > 0 ? RESP.ok("新增成功") : RESP.error("新增失败");
    }

    @Override
    public RESP deleteCounselor(String counselorId) {
        int result = counselorInfoMapper.deleteById(counselorId);
        return result > 0 ? RESP.ok("删除成功") : RESP.error("删除失败");
    }

    @Override
    public RESP updateCounselor(CounselorInfo counselor) {
        int result = counselorInfoMapper.update(counselor);
        return result > 0 ? RESP.ok("更新成功") : RESP.error("更新失败");
    }

    @Override
    public RESP getCounselorById(String counselorId) {
        CounselorInfo counselor = counselorInfoMapper.selectById(counselorId);
        return counselor != null ? RESP.ok(counselor) : RESP.error("咨询师不存在");
    }

    @Override
    public RESP getAllCounselors() {
        List<CounselorInfo> list = counselorInfoMapper.selectAll();
        return RESP.ok(list);
    }

    @Override
    public RESP getCounselorsPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<CounselorInfo> list = counselorInfoMapper.selectPage(offset, pageSize);
        int total = counselorInfoMapper.countAll();
        return RESP.ok(list, pageNum, total);
    }

    @Override
    public RESP getCounselorCount() {
        int count = counselorInfoMapper.countAll();
        return RESP.ok(count);
    }
}
