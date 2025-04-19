package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentLeaveService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentLeave")

public class StudentLeaveController {
    private StudentLeaveService studentLeaveService;

    public StudentLeaveController(StudentLeaveService studentLeaveService) {
        this.studentLeaveService = studentLeaveService;
    }

    @PostMapping("/getStudentLeaveList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentLeaveList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        return CommonMethod.getReturnData(studentLeaveService.getStudentLeaveList(numName));
    }
    @PostMapping("/studentLeaveDelete")
    public DataResponse studentLeaveDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.studentLeaveDelete(dataRequest);
    }

    @PostMapping("/getStudentLeaveInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentLeaveInfo(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.getStudentLeaveInfo(dataRequest);
    }

    @PostMapping("/studentLeaveEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentLeaveEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentLeaveService.studentLeaveEditSave(dataRequest);
    }


}
