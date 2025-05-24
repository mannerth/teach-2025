package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HomeworkService {
    private final CourseRepository courseRepository;
    private final HomeworkRepository homeworkRepository;

    public HomeworkService(CourseRepository courseRepository, HomeworkRepository homeworkRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.homeworkRepository = homeworkRepository;

    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> cList = courseRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for(Course c : cList) {
            itemList.add(new OptionItem(c.getCourseId(), c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getHomeworkList(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null) {
            courseId = 0;
        }
        List<Homework> hList = homeworkRepository.findByCourseId(courseId);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for(Homework h : hList) {
            m = new HashMap<>();
            m.put("homeworkId", h.getHomeworkId()+"");
            m.put("courseId", h.getCourse().getCourseId()+"");
            m.put("courseNum", h.getCourse().getNum());
            m.put("courseName", h.getCourse().getName());
            m.put("content", h.getContent());
            String cs = DateTimeTool.parseDateTime(h.getHomeworkReleasingTime(), "yyyy-MM-dd HH:mm:ss");
            m.put("homeworkReleasingTime", cs);
            String ds = DateTimeTool.parseDateTime(h.getHomeworkDeadline(), "yyyy-MM-dd HH:mm:ss");
            m.put("homeworkDeadline", ds);
            System.out.println(m);

            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    //布置新的作业时，后端从前端homeworkTableController那里获取新作业的相关信息
    public DataResponse homeworkSave(DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer courseNum = dataRequest.getInteger("courseNum");
        String courseName = dataRequest.getString("courseName");
        String content = dataRequest.getString("content");


        String releasingTimeStr = dataRequest.getString("releasingTime");
        System.out.println(releasingTimeStr);
        Date homeworkReleasingTime = DateTimeTool.formatDateTime(releasingTimeStr, "yyyy-MM-dd HH:mm:ss");

        String deadlineStr = dataRequest.getString("deadline");
        System.out.println(deadlineStr);
        Date homeworkDeadline = DateTimeTool.formatDateTime(deadlineStr, "yyyy-MM-dd'T'HH:mm:ss");


        Optional<Homework> op;
        Homework h = null;
        if(homeworkId != null) {
            op = homeworkRepository.findById(homeworkId);
            if(op.isPresent()) {
                h = op.get();
            }
        }
        if(h == null) {
            h = new Homework();
            h.setCourse(courseRepository.findById(courseId).get());
        }
        h.setHomeworkId(homeworkId);
        h.setHomeworkReleasingTime(homeworkReleasingTime);
        h.setHomeworkDeadline(homeworkDeadline);
        h.setContent(content);
        homeworkRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse homeworkDelete(DataRequest dataRequest) {
        Integer homeworkId = dataRequest.getInteger("homeworkId");
        Optional<Homework> op;
        Homework h = null;
        if(homeworkId != null){
            op = homeworkRepository.findById(homeworkId);
            if(op.isPresent()) {
                h = op.get();
                homeworkRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}