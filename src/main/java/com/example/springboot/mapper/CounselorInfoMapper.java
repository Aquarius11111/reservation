package com.example.springboot.mapper;

import com.example.springboot.entity.CounselorInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CounselorInfoMapper {

    /** 新增咨询师信息 */
    @Insert("INSERT INTO counselor_info(counselor_id, specialty, orientation, title, location, service_time, motto, avatar_url) " +
            "VALUES(#{counselorId}, #{specialty}, #{orientation}, #{title}, #{location}, #{serviceTime}, #{motto}, #{avatarUrl})")
    int insert(CounselorInfo counselor);

    /** 删除咨询师信息 */
    @Delete("DELETE FROM counselor_info WHERE counselor_id = #{counselorId}")
    int deleteById(String counselorId);

    /** 更新咨询师信息 */
    @Update("UPDATE counselor_info SET specialty=#{specialty}, orientation=#{orientation}, title=#{title}, location=#{location}, " +
            "service_time=#{serviceTime}, motto=#{motto}, avatar_url=#{avatarUrl} WHERE counselor_id=#{counselorId}")
    int update(CounselorInfo counselor);

    /** 查询单个咨询师信息 */
    @Select("SELECT * FROM counselor_info WHERE counselor_id = #{counselorId}")
    CounselorInfo selectById(String counselorId);

    /** 查询所有咨询师信息 */
    @Select("SELECT * FROM counselor_info")
    List<CounselorInfo> selectAll();

    /** 分页查询咨询师信息 */
    @Select("SELECT * FROM counselor_info LIMIT #{offset}, #{pageSize}")
    List<CounselorInfo> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /** 查询咨询师总数 */
    @Select("SELECT COUNT(*) FROM counselor_info")
    int countAll();
}
