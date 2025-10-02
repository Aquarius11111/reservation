package com.example.springboot.mapper;

import com.example.springboot.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SysUserMapper {

    /** 新增用户 */
    @Insert("INSERT INTO sys_user(user_id, user_name, user_role, dept_name, password, status, create_user, create_time, update_time) " +
            "VALUES(#{userId}, #{userName}, #{userRole}, #{deptName}, #{password}, #{status}, #{createUser}, #{createTime}, #{updateTime})")
    int insert(SysUser user);

    /** 删除用户 */
    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    int deleteById(@Param("userId") String userId);

    /** 更新用户信息 */
    @Update("UPDATE sys_user SET user_name=#{userName}, user_role=#{userRole}, dept_name=#{deptName}, password=#{password}, status=#{status}, update_time=#{updateTime} " +
            "WHERE user_id=#{userId}")
    int update(SysUser user);

    /** 查询单个用户 */
    @Select("SELECT * FROM sys_user WHERE user_id=#{userId}")
    SysUser selectById(@Param("userId") String userId);

    /** 查询所有用户 */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC")
    List<SysUser> selectAll();

    /** 分页查询 */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<SysUser> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    /** 统计用户总数 */
    @Select("SELECT COUNT(*) FROM sys_user")
    int countAll();

    // 查询用户密码
    @Select("SELECT password FROM sys_user WHERE user_id=#{userId}")
    String selectPasswordByUserId(@Param("userId") String userId);

    // 更新用户密码
    @Update("UPDATE sys_user SET password=#{newPassword}, update_time=#{updateTime} WHERE user_id=#{userId}")
    int updatePassword(@Param("userId") String userId,
                       @Param("newPassword") String newPassword,
                       @Param("updateTime") LocalDateTime updateTime);
}
