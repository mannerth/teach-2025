package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentCourseListService;
import cn.edu.sdu.java.server.services.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentCourseList")
public class StudentCourseListController {
    private final StudentCourseListService studentCourseListService;

    public StudentCourseListController(StudentCourseListService studentCourseListService) {
        this.studentCourseListService = studentCourseListService;
    }

    @PostMapping("/getStudentCourseList")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        return studentCourseListService.getStudentCourseList(dataRequest);
    }

    @PostMapping("/addCourse")
    public DataResponse addCourse(@Valid @RequestBody DataRequest dataRequest){
        return studentCourseListService.addStudentCourseList(dataRequest);
    }

    @PostMapping("/deleteCourse")
    public DataResponse deleteCourse(@Valid@RequestBody DataRequest dataRequest){
        return studentCourseListService.deleteCourseList(dataRequest);
    }
}
