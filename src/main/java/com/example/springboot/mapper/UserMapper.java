package com.example.springboot.mapper;

import com.example.springboot.dto.LoginResponseDTO;
import com.example.springboot.vo.LoginVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    // 根据用户ID查询登录所需用户信息（直接返回DTO，减少数据冗余）
    LoginVO selectLoginUserByUserId(String userId);

    // 查询用户密码（单独查询密码，避免密码字段暴露在响应DTO中）
    String selectPasswordByUserId(String userId);

    String getLastCounselTime(String studentId);
    String getLastEvaluateTime(String studentId);
}