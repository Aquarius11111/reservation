package com.example.springboot.service;

import com.example.springboot.entity.SysUser;
import com.example.springboot.utils.RESP;

import java.util.List;

public interface SysUserService {

    RESP addUser(SysUser user);

    RESP deleteUser(String userId);

    RESP updateUser(SysUser user);

    RESP getUserById(String userId);

    RESP getAllUsers();

    RESP getUsersPage(int pageNum, int pageSize);

    RESP getUserCount();

    // 修改密码
    RESP changePassword(String userId, String oldPassword, String newPassword);
}
