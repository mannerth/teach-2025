package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.StudentCourseList;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentCourseListRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentCourseListService {
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final StudentCourseListRepository studentCourseListRepository;

    public StudentCourseListService(PersonRepository personRepository, StudentCourseListRepository studentCourseListRepository) {
        this.personRepository = personRepository;
        this.studentCourseListRepository = studentCourseListRepository;
    }

    /// 相应前端课表列表请求
    public DataResponse getStudentCourseList(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("personId");
        StudentCourseList s = getTheObject(id);
        Map<String, String> ret = getMapFromString(s.getCourseList());
        return CommonMethod.getReturnData(ret);
    }
    /// 添加课表内容
    public DataResponse addStudentCourseList(DataRequest dataRequest){
        String time = dataRequest.getString("time");        //获取信息
        String content = dataRequest.getString("content");
        int id = dataRequest.getInteger("personId");
        StudentCourseList studentCourseList = getTheObject(id); //请求对象
        Map<String, String> map = getMapFromString(studentCourseList.getCourseList());
        map.put(time,content);                                  //添加信息
        ObjectMapper objectMapper = new ObjectMapper();         //转为json存储
        try{
            studentCourseList.setCourseList(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            return CommonMethod.getReturnMessage(400,"map转String错误");
        }
        studentCourseListRepository.saveAndFlush(studentCourseList);//保存
        return CommonMethod.getReturnMessage(200,"添加成功");
    }

    /// 删除课表
    public DataResponse deleteCourseList(DataRequest dataRequest){

        return CommonMethod.getReturnMessageOK();
    }

    /// 根据id查询学生
    private StudentCourseList getTheObject(int id){
        Optional<StudentCourseList> optional= studentCourseListRepository.findByPersonId(id);
        //StudentCourseList s = optional.orElse(null);
        StudentCourseList s;
        if(optional.isEmpty()){
            s = new StudentCourseList();
            studentCourseListRepository.saveAndFlush(s);
        }else{
            s = optional.get();
        }
        return s;
    }

    /// 封装了一个String转Map函数
    private Map<String, String> getMapFromString(String s){
        if(s==null||s.isEmpty())return new HashMap<String,String>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> ret;
        try{
            ret = objectMapper.readValue(s,Map.class);
        } catch (JsonProcessingException e) {
            ret = new HashMap<>();
        }
        return ret;
    }

}
