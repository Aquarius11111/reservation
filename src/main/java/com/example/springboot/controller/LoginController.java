package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.dto.LoginRequestDTO;
import com.example.springboot.dto.LoginResponseDTO;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 登录控制器（MD5加密版本）
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数（包含userId和password）
     * @return 统一响应结果
     */
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginRequestDTO loginRequest) {
        // 1. 获取请求参数
        String userId = loginRequest.getUserId();
        String inputPassword = loginRequest.getPassword();

        // 2. 根据用户ID查询用户信息（不含密码）
        LoginResponseDTO loginUser = userService.getLoginUserByUserId(userId);
        if (loginUser == null) {
            // 用户不存在（使用自定义错误码404）
            return Result.error(404, "用户不存在");
        }

        // 3. 查询数据库中的加密密码
        String dbPassword = userService.getPasswordByUserId(userId);
        if (dbPassword == null) {
            // 理论上不会走到这里（因为用户已存在），做双重保险
            return Result.error(404, "用户信息异常");
        }

        // 4. 对输入密码进行MD5加密并与数据库密码比对
        String encryptedInputPassword = md5Encrypt(inputPassword);
        if (!encryptedInputPassword.equals(dbPassword)) {
            // 密码错误（使用自定义错误码401）
            return Result.error(401, "密码错误");
        }

        // 5. 登录成功，返回用户信息
        return Result.success(loginUser);
    }

    /**
     * MD5加密方法
     * @param password 明文密码
     * @return 加密后的32位小写字符串
     */
    private String md5Encrypt(String password) {
        try {
            // 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 对密码字节数组进行加密
            byte[] digest = md.digest(password.getBytes());

            // 转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将字节转换为16进制（保证两位数，不足补0）
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 加密算法不存在时抛出运行时异常（理论上不会发生）
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}