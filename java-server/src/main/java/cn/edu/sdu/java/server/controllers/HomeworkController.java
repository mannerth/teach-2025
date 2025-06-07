package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.HomeworkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    private final HomeworkService homeworkService;
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }
    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.getCourseItemOptionList(dataRequest);
    }

    @PostMapping("/getCourseExItemOptionList")
    public OptionItemList getCourseExItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.getCourseExItemOptionList(dataRequest);
    }

//    @PostMapping("/getStudentItemOptionList")
//    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
//        return homeworkService.getStudentItemOptionList(dataRequest);
//    }
    @PostMapping("/getHomeworkList")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.getHomeworkList(dataRequest);
    }
    @PostMapping("/homeworkSave")
    public DataResponse homeworkSave(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.homeworkSave(dataRequest);
    }
    @PostMapping("/homeworkDelete")
    public DataResponse homeworkDelete(@Valid @RequestBody DataRequest dataRequest) {
        return homeworkService.homeworkDelete(dataRequest);
    }


}