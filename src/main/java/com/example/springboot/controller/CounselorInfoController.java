package com.example.springboot.controller;

import com.example.springboot.entity.CounselorInfo;
import com.example.springboot.service.CounselorInfoService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/counselor/info")
public class CounselorInfoController {

    @Autowired
    private CounselorInfoService counselorInfoService;

    /** 新增咨询师信息 */
    @PostMapping("/add")
    public RESP addCounselor(@RequestBody CounselorInfo counselor) {
        return counselorInfoService.addCounselor(counselor);
    }

    /** 删除咨询师信息 */
    @DeleteMapping("/delete/{counselorId}")
    public RESP deleteCounselor(@PathVariable String counselorId) {
        return counselorInfoService.deleteCounselor(counselorId);
    }

    /** 更新咨询师信息 */
    @PutMapping("/update")
    public RESP updateCounselor(@RequestBody CounselorInfo counselor) {
        return counselorInfoService.updateCounselor(counselor);
    }

    /** 查询单个咨询师信息 */
    @GetMapping("/get/{counselorId}")
    public RESP getCounselor(@PathVariable String counselorId) {
        return counselorInfoService.getCounselorById(counselorId);
    }

    /** 查询所有咨询师信息 */
    @GetMapping("/all")
    public RESP getAllCounselors() {
        return counselorInfoService.getAllCounselors();
    }

    /** 分页查询咨询师信息 */
    @GetMapping("/page")
    public RESP getCounselorsPage(@RequestParam int pageNum, @RequestParam int pageSize) {
        return counselorInfoService.getCounselorsPage(pageNum, pageSize);
    }

    /** 查询咨询师总数 */
    @GetMapping("/count")
    public RESP getCounselorCount() {
        return counselorInfoService.getCounselorCount();
    }

    /** 模糊搜索+分页查询 */
    @GetMapping("/search")
    public RESP searchCounselors(@RequestParam String keyword,
                                 @RequestParam int pageNum,
                                 @RequestParam int pageSize) {
        return counselorInfoService.searchCounselors(keyword, pageNum, pageSize);
    }

}
