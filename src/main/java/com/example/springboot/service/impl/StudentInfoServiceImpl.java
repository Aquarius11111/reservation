package com.example.springboot.service.impl;

import com.example.springboot.entity.StudentInfo;
import com.example.springboot.mapper.StudentInfoMapper;
import com.example.springboot.service.StudentInfoService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentInfoServiceImpl implements StudentInfoService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Override
    public RESP addStudent(StudentInfo student) {
        int result = studentInfoMapper.insert(student);
        return result > 0 ? RESP.ok("新增成功") : RESP.error("新增失败");
    }

    @Override
    public RESP deleteStudent(String studentId) {
        int result = studentInfoMapper.deleteById(studentId);
        return result > 0 ? RESP.ok("删除成功") : RESP.error("删除失败");
    }

    @Override
    public RESP updateStudent(StudentInfo student) {
        int result = studentInfoMapper.update(student);
        return result > 0 ? RESP.ok("更新成功") : RESP.error("更新失败");
    }

    @Override
    public RESP getStudentById(String studentId) {
        StudentInfo student = studentInfoMapper.selectById(studentId);
        return student != null ? RESP.ok(student) : RESP.error("学生不存在");
    }

    @Override
    public RESP getAllStudents() {
        List<StudentInfo> students = studentInfoMapper.selectAll();
        return RESP.ok(students);
    }

    @Override
    public RESP getStudentsPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<StudentInfo> students = studentInfoMapper.selectPage(offset, pageSize);
        int total = studentInfoMapper.countAll();
        return RESP.ok(students, pageNum, total);
    }

    @Override
    public RESP getStudentCount() {
        int count = studentInfoMapper.countAll();
        return RESP.ok(count);
    }

    @Override
    public RESP searchStudents(String studentId, Integer gender, String college, String major, String className, Integer riskLevel, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<StudentInfo> list = studentInfoMapper.searchPage(studentId, gender, college, major, className, riskLevel, offset, pageSize);
        int total = studentInfoMapper.countSearch(studentId, gender, college, major, className, riskLevel);
        return RESP.ok(list, pageNum, total);
    }
}
