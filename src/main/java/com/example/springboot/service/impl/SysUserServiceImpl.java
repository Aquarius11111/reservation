package com.example.springboot.service.impl;

import com.example.springboot.entity.SysUser;
import com.example.springboot.mapper.SysUserMapper;
import com.example.springboot.service.SysUserService;
import com.example.springboot.utils.RESP;
import com.example.springboot.utils.DU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public RESP addUser(SysUser user) {
        // MD5 加密密码
        String rawPassword = user.getPassword();
        String md5Password = md5(rawPassword);
        user.setPassword(md5Password);

        // 设置创建和更新时间
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());

        int result = sysUserMapper.insert(user);
        return result > 0 ? RESP.ok("新增成功") : RESP.error("新增失败");
    }

    @Override
    public RESP deleteUser(String userId) {
        int result = sysUserMapper.deleteById(userId);
        return result > 0 ? RESP.ok("删除成功") : RESP.error("删除失败");
    }

    @Override
    public RESP updateUser(SysUser user) {
        // 如果密码不为空，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(md5(user.getPassword()));
        }

        // 更新修改时间
        user.setUpdateTime(java.time.LocalDateTime.now());

        int result = sysUserMapper.update(user);
        return result > 0 ? RESP.ok("更新成功") : RESP.error("更新失败");
    }

    @Override
    public RESP getUserById(String userId) {
        SysUser user = sysUserMapper.selectById(userId);
        return user != null ? RESP.ok(user) : RESP.error("用户不存在");
    }

    @Override
    public RESP getAllUsers() {
        List<SysUser> users = sysUserMapper.selectAll();
        return RESP.ok(users);
    }

    @Override
    public RESP getUsersPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SysUser> users = sysUserMapper.selectPage(offset, pageSize);
        int total = sysUserMapper.countAll();
        return RESP.ok(users, pageNum, total);
    }

    @Override
    public RESP getUserCount() {
        int count = sysUserMapper.countAll();
        return RESP.ok(count);
    }


    /**
     * 修改用户密码
     */
    @Override
    public RESP changePassword(String userId, String oldPassword, String newPassword) {
        // 查询数据库中原始密码
        String dbPassword = sysUserMapper.selectPasswordByUserId(userId);
        if (dbPassword == null) {
            return RESP.error("用户不存在");
        }

        // 比较旧密码（前端传来的密码加密后与数据库一致）
        if (!dbPassword.equals(md5(oldPassword))) {
            return RESP.error("旧密码错误");
        }

        // 加密新密码并更新
        int result = sysUserMapper.updatePassword(userId, md5(newPassword), java.time.LocalDateTime.now());
        return result > 0 ? RESP.ok("密码修改成功") : RESP.error("密码修改失败");
    }
    /**
     * MD5 加密工具方法
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}
