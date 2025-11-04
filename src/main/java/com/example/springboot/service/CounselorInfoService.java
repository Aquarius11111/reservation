package com.example.springboot.service;

import com.example.springboot.entity.CounselorInfo;
import com.example.springboot.utils.RESP;

public interface CounselorInfoService {

    RESP addCounselor(CounselorInfo counselor);

    RESP deleteCounselor(String counselorId);

    RESP updateCounselor(CounselorInfo counselor);

    RESP getCounselorById(String counselorId);

    RESP getAllCounselors();

    RESP getCounselorsPage(int pageNum, int pageSize);

    RESP getCounselorCount();

    RESP searchCounselors(String keyword, int pageNum, int pageSize);

}
