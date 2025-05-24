package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CourseExService {
    private final CourseExRepository courseExRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;

    public CourseExService(CourseExRepository courseExRepository, CourseRepository courseRepository, TeacherRepository teacherRepository, StudentRepository studentRepository, StudentCourseRepository studentCourseRepository){
        this.courseExRepository = courseExRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    /*
    * 请求体：
    *课程          courseId
    *教师          teacherNum
    *课序号        course_num
    *上课地点       place
    *上课时间       time
    *最大人数       max_stu_num
    *(课程信息)     information   该字段可拓展
    * */
    /// 发布新的选课
    public DataResponse postNewCourse(DataRequest dataRequest){
        Map<String, Object> req = dataRequest.getData();
        Integer courseExId = CommonMethod.getInteger(req, "courseExId");
        Integer courseNum = CommonMethod.getInteger(req, "courseId");
        String teacherNum = CommonMethod.getString(req, "teacherNum");
        String course_num = CommonMethod.getString(req, "course_num");
        String place = CommonMethod.getString(req, "place");
        String time = CommonMethod.getString(req, "time");
        String information = CommonMethod.getString(req, "information");
        Integer max_stu_num = CommonMethod.getInteger(req, "max_stu_num");
        Integer choose = CommonMethod.getInteger(req, "state");
        DataResponse res;
        Optional<Course> _course = courseRepository.findById(courseNum);
        Optional<Teacher> _teacher = teacherRepository.findByPersonNum(teacherNum);
        if(_course.isEmpty()|| _teacher.isEmpty()){
            res = new DataResponse(1,null,"寻找课程或教师时发生错误");
            return res;
        }
        Course course = _course.get();
        Teacher teacher = _teacher.get();
        CourseEx c;
        if(courseExId!=null){
            c = courseExRepository.findById(courseExId).orElse(new CourseEx());
        }else{
            c = new CourseEx();
        }
        c.setCourse(course);
        c.setTeacher(teacher);
        c.setCourse_num(course_num);
        c.setPlace(place);
        c.setTime_inf(time);
        c.setMax_stu_num(max_stu_num);
        c.setInformation(information);
        c.setIs_choosable(choose!=null&&choose == 1);
        courseExRepository.saveAndFlush(c);
        res = new DataResponse(0,null,"发布成功，当前为可选状态");
        return res;
    }

    /*
    * 请求参数：
    * course_num
    * */
    /// 修改可选状态
    public DataResponse changeCourseChoosable(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        String num = CommonMethod.getString(map, "course_num");
        CourseEx courseEx = courseExRepository.findByCourse_num(num).orElse(null);
        if(courseEx==null){
            return new DataResponse(1,null,"发生错误1");
        }
        courseEx.setIs_choosable(!courseEx.getIs_choosable());
        return new DataResponse(0,null,"修改成功");
    }

    /*
     * 请求参数：
     * course_num
     */
    /// 删除课程
    public DataResponse deleteCourse(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        Integer id = CommonMethod.getInteger(map,"courseExId");
        CourseEx courseEx = courseExRepository.findById(id).orElse(null);
        if(courseEx==null){
            return new DataResponse(1,null,"发生错误2");
        }
        courseExRepository.delete(courseEx);
        return new DataResponse(0,null,"删除成功");
    }

    /// 获取当前所有课程
    public DataResponse getCourseExList(DataRequest dataRequest){
        List<CourseEx> list = courseExRepository.findAll();
        return getResponseFromList(list);
    }
    /// 获取当前所有可选课程
    public DataResponse getChoosableCourseList(DataRequest dataRequest){
        List<CourseEx> list = courseExRepository.findChoosableCourseExList();
        return getResponseFromList(list);
    }
    /// 获取当前所有不可选课程
    public DataResponse getNotChoosableCourseList(DataRequest dataRequest){
        return getResponseFromList(courseExRepository.findNotChoosableCourseExList());
    }
    /// 按老师和课程查询
    public DataResponse getCourseByTeacherAndCourse(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        Integer id = CommonMethod.getInteger(map, "courseId");
        if(id==null) id = 0;
        String num = CommonMethod.getString(map, "num");
        if(num==null||num.equals("0")) num = "";
        Boolean is = (Boolean) map.get("is_choosable");
        return getResponseFromList(courseExRepository.findCourseByTeacherCourseId(id,num,is));
    }
    private DataResponse getResponseFromList(List<CourseEx> list){
        List< Map<String, Object> > ret = new ArrayList<>();
        for( CourseEx c : list){
            Map<String, Object> map = new HashMap<>();
            map.put("courseExId",c.getCourseExId());
            map.put("courseId",c.getCourse().getCourseId());
            map.put("course_num", c.getCourse_num());
            map.put("courseNum", c.getCourse().getNum());
            map.put("courseName", c.getCourse().getName());
            map.put("credit", c.getCourse().getCredit());
            map.put("is_choosable", c.getIs_choosable()?"可选":"不可选");
            map.put("max_stu_num", c.getMax_stu_num());
            map.put("place", c.getPlace());
            map.put("time", c.getTime_inf());
            map.put("information", c.getInformation());
            map.put("teacher_name", c.getTeacher().getPerson().getName());
            map.put("teacher_num", c.getTeacher().getPerson().getNum());
            ret.add(map);
        }
        return new DataResponse(0,ret,"");
    }

    public OptionItemList getTeacherOptionItemList(DataRequest dataRequest){
        List<Teacher> list = teacherRepository.findAll();
        List<OptionItem> ret = new ArrayList<>();
        for( Teacher i : list ){
            ret.add(new OptionItem(i.getPersonId(), i.getPerson().getNum(),i.getPerson().getNum()+"-"+i.getPerson().getName()));
        }
        return new OptionItemList(0,ret);
    }

    /// 学生获取已经选的课程
    public DataResponse getSelectedCourse(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        Integer id = CommonMethod.getInteger(map, "personId");
        if(id==null)
            return CommonMethod.getReturnMessage(0,"信息不全");
        List<CourseEx> list = courseExRepository.findByStudent(id);
        return getResponseFromList(list);
    }

    /// 学生选课        请求参数 personId   courseExId
    public DataResponse studentSelectCourse(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        Integer personId = CommonMethod.getInteger(map, "personId");
        Integer courseExId = CommonMethod.getInteger(map, "courseExId");
        if(personId==null||courseExId==null){
            return CommonMethod.getReturnMessage(0,"信息不全");
        }
        Optional<Student> _student = studentRepository.findByPersonPersonId(personId);
        Optional<CourseEx> _courseEx = courseExRepository.findById(courseExId);
        if(_courseEx.isEmpty()||_student.isEmpty()){
            return CommonMethod.getReturnMessage(0,"寻找时发生错误");
        }
        Student student = _student.get();
        CourseEx courseEx = _courseEx.get();
        for(StudentCourse sc : student.getStudentCourses()){
            if(sc.getCourseEx()==courseEx){
                return CommonMethod.getReturnMessage(0,"不能重复选课！");
            }
        }
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourseEx(courseEx);
        studentCourse.setStudent(student);
        studentCourse.setSelectedTime(LocalDateTime.now());
        studentCourseRepository.save(studentCourse);
        student.getStudentCourses().add(studentCourse);
        courseEx.getStudentCourses().add(studentCourse);
        studentRepository.save(student);
        courseExRepository.save(courseEx);
        return CommonMethod.getReturnMessage(1,"选课成功!");
    }

    public DataResponse studentCancelSelectCourse(DataRequest dataRequest){
        Map<String, Object> map = dataRequest.getData();
        Integer personId = CommonMethod.getInteger(map, "personId");
        Integer courseExId = CommonMethod.getInteger(map, "courseExId");
        if(personId==null||courseExId==null){
            return CommonMethod.getReturnMessage(0,"信息不全");
        }
        Optional<Student> _student = studentRepository.findByPersonPersonId(personId);
        Optional<CourseEx> _courseEx = courseExRepository.findById(courseExId);
        if(_courseEx.isEmpty()||_student.isEmpty()){
            return CommonMethod.getReturnMessage(0,"寻找时发生错误");
        }
        Student student = _student.get();
        CourseEx courseEx = _courseEx.get();
        for(StudentCourse sc : student.getStudentCourses()){
            if(sc.getCourseEx()==courseEx){
                student.getStudentCourses().remove(sc);
                courseEx.getStudentCourses().remove(sc);
                break;
            }
        }
        studentRepository.save(student);
        courseExRepository.save(courseEx);
        return CommonMethod.getReturnMessage(1,"退课成功!");
    }
}