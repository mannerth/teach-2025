package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.BaseService;
import cn.edu.sdu.java.server.services.StudentHomeworkService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentHomework")
@Slf4j
public class StudentHomeworkController {
    private final StudentHomeworkService studentHomeworkService;
    private final BaseService baseService;

    public StudentHomeworkController(StudentHomeworkService studentHomeworkService, BaseService baseService) {
        this.studentHomeworkService = studentHomeworkService;
        this.baseService = baseService;
    }

    @PostMapping("/getStudentHomeworkList")
    public DataResponse getStudentHomeworkList(@Valid@RequestBody DataRequest dataRequest){
        return studentHomeworkService.getStudentHomeworkList(dataRequest);
    }

    @PostMapping( "/submitHomework")
    public DataResponse uploadPhotoBlob(@RequestBody byte[] barr,
                                        @RequestParam(name = "uploader") String uploader,
                                        @RequestParam(name = "remoteFile") String homeworkId,
                                        @RequestParam(name = "fileName") String fileName) {
        return studentHomeworkService.submitHomework(barr, homeworkId);
    }

    @PostMapping("/getBlobByteDataByStudentHomework")
    public ResponseEntity<StreamingResponseBody> getBlobByteDataByStudentHomework(@Valid @RequestBody DataRequest dataRequest) {
        return studentHomeworkService.getBlobByteDataByStudentHomework(dataRequest);
    }
}
