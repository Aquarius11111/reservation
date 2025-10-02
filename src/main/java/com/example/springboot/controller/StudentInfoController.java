package com.example.springboot.controller;

import com.example.springboot.entity.StudentInfo;
import com.example.springboot.service.StudentInfoService;
import com.example.springboot.utils.RESP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/info")
public class StudentInfoController {

    @Autowired
    private StudentInfoService studentInfoService;

    /** 新增学生信息 */
    @PostMapping("/add")
    public RESP addStudent(@RequestBody StudentInfo student) {
        return studentInfoService.addStudent(student);
    }

    /** 删除学生信息 */
    @DeleteMapping("/delete/{studentId}")
    public RESP deleteStudent(@PathVariable String studentId) {
        return studentInfoService.deleteStudent(studentId);
    }

    /** 更新学生信息 */
    @PutMapping("/update")
    public RESP updateStudent(@RequestBody StudentInfo student) {
        return studentInfoService.updateStudent(student);
    }

    /** 查询单个学生信息 */
    @GetMapping("/get/{studentId}")
    public RESP getStudent(@PathVariable String studentId) {
        return studentInfoService.getStudentById(studentId);
    }

    /** 查询所有学生信息 */
    @GetMapping("/all")
    public RESP getAllStudents() {
        return studentInfoService.getAllStudents();
    }

    /** 分页查询学生信息 */
    @GetMapping("/page")
    public RESP getStudentsPage(@RequestParam int pageNum, @RequestParam int pageSize) {
        return studentInfoService.getStudentsPage(pageNum, pageSize);
    }

    /** 查询学生总数 */
    @GetMapping("/count")
    public RESP getStudentCount() {
        return studentInfoService.getStudentCount();
    }
}
