package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentCourse;
import cn.edu.sdu.java.server.models.StudentHomework;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.repositorys.StudentHomeworkRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentHomeworkService {
    private final StudentHomeworkRepository studentHomeworkRepository;
    private final CourseRepository courseRepository;
    private final CourseExService courseExService;
    private final HomeworkRepository homeworkRepository;
    private final StudentRepository studentRepository;

    public StudentHomeworkService(StudentHomeworkRepository studentHomeworkRepository, CourseRepository courseRepository, CourseExService courseExService, HomeworkRepository homeworkRepository, StudentRepository studentRepository) {
        this.studentHomeworkRepository = studentHomeworkRepository;
        this.courseRepository = courseRepository;
        this.courseExService = courseExService;
        this.homeworkRepository = homeworkRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getStudentHomeworkList(DataRequest dataRequest){
        Map map = dataRequest.getData();
        Integer id = CommonMethod.getInteger(map, "personId");
        Optional<Student> _s = studentRepository.findByPersonPersonId(id);
        if(_s.isEmpty()){
            return CommonMethod.getReturnMessage(1,"未找到学生");
        }
        Student student = _s.get();
        List<StudentCourse> list = student.getStudentCourses();
        List<Homework> homeworkList = new ArrayList<>();
        for(var i : list){
            Integer courseExId =  i.getCourseEx().getCourseExId();
            List<Homework> temp = homeworkRepository.findByCourseEx(courseExId);
            homeworkList.addAll(temp);
        }
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for(Homework h : homeworkList) {
            m = new HashMap<>();
            m.put("homeworkId", h.getHomeworkId()+"");
            m.put("courseId", h.getCourse().getCourseId()+"");
            m.put("courseNum", h.getCourse().getNum());
            m.put("courseName", h.getCourse().getName());
            m.put("courseExId", h.getCourseEx()+"");
            m.put("course_num", h.getCourseEx().getCourse_num());
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

    public DataResponse submitHomework(byte[] barr,String homeworkId){
        try {
            Optional<StudentHomework> op = 
            if(op.isEmpty())
                return CommonMethod.getReturnMessageError("作业不存在！");
            Homework h = op.get();
            h.setPhoto(barr);
            homeworkRepository.save(h);
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("上传错误");
        }
    }

}
