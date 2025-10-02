package com.example.springboot.service;

import com.example.springboot.entity.StudentInfo;
import com.example.springboot.utils.RESP;

public interface StudentInfoService {

    RESP addStudent(StudentInfo student);

    RESP deleteStudent(String studentId);

    RESP updateStudent(StudentInfo student);

    RESP getStudentById(String studentId);

    RESP getAllStudents();

    RESP getStudentsPage(int pageNum, int pageSize);

    RESP getStudentCount();
}
