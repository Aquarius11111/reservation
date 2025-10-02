package com.example.springboot.controller;

import com.example.springboot.entity.SysUser;
import com.example.springboot.service.SysUserService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /** 新增用户 */
    @PostMapping("/add")
    public RESP addUser(@RequestBody SysUser user) {
        return sysUserService.addUser(user);
    }

    /** 删除用户 */
    @DeleteMapping("/delete/{userId}")
    public RESP deleteUser(@PathVariable String userId) {
        return sysUserService.deleteUser(userId);
    }

    /** 更新用户信息 */
    @PutMapping("/update")
    public RESP updateUser(@RequestBody SysUser user) {
        return sysUserService.updateUser(user);
    }

    /** 查询单个用户 */
    @GetMapping("/get/{userId}")
    public RESP getUser(@PathVariable String userId) {
        return sysUserService.getUserById(userId);
    }

    /** 查询所有用户 */
    @GetMapping("/all")
    public RESP getAllUsers() {
        return sysUserService.getAllUsers();
    }

    /** 分页查询 */
    @GetMapping("/page")
    public RESP getUsersPage(@RequestParam int pageNum, @RequestParam int pageSize) {
        return sysUserService.getUsersPage(pageNum, pageSize);
    }

    /** 查询用户总数 */
    @GetMapping("/count")
    public RESP getUserCount() {
        return sysUserService.getUserCount();
    }

    /** 修改密码 */
    @PutMapping("/changePassword")
    public RESP changePassword(@RequestParam String userId,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword) {
        return sysUserService.changePassword(userId, oldPassword, newPassword);
    }
}
