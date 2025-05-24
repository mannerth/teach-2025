package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.CourseExService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/CourseEx")
public class CourseExController {
    private final CourseExService courseExService;

    public CourseExController(CourseExService courseExService) {
        this.courseExService = courseExService;
    }

    @PostMapping("/postNewCourse")
    public DataResponse postNewCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.postNewCourse(dataRequest);
    }

    @PostMapping("/changeCourseChoosable")
    public DataResponse changeCourseChoosable(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.changeCourseChoosable(dataRequest);
    }

    @PostMapping("/deleteCourse")
    public DataResponse deleteCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.deleteCourse(dataRequest);
    }

    @PostMapping("/getCourseExList")
    public DataResponse getCourseExList(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.getCourseExList(dataRequest);
    }

    @PostMapping("/getChoosableCourseList")
    public DataResponse getChoosableCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.getChoosableCourseList(dataRequest);
    }

    @PostMapping("/getNotChoosableCourseList")
    public DataResponse getNotChoosableCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.getNotChoosableCourseList(dataRequest);
    }

    @PostMapping("/getTeacherOptionItemList")
    public OptionItemList getTeacherOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        return courseExService.getTeacherOptionItemList(dataRequest);
    }

    @PostMapping("/findByTeacherCourse")
    public DataResponse findByTeacherCourse(@Valid@RequestBody DataRequest dataRequest){
        return courseExService.getCourseByTeacherAndCourse(dataRequest);
    }

    @PostMapping("/studentSelectCourse")
    public DataResponse studentSelectCourse(@Valid@RequestBody DataRequest dataRequest){
        return courseExService.studentSelectCourse(dataRequest);
    }

    @PostMapping("/studentCancelSelectCourse")
    public DataResponse studentCancelSelectCourse(@Valid@RequestBody DataRequest dataRequest){
        return courseExService.studentCancelSelectCourse(dataRequest);
    }

    @PostMapping("/getSelectedCourse")
    public DataResponse getSelectedCourse(@Valid@RequestBody DataRequest dataRequest){
        return courseExService.getSelectedCourse(dataRequest);
    }

}