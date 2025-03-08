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

    public DataResponse getStudentCourseList(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("personId");
        Optional<StudentCourseList> optional= studentCourseListRepository.findByPersonId(id);
        StudentCourseList s = optional.orElse(null);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> ret;
        try{
            ret = objectMapper.readValue(s.getCourseList(),Map.class);
        } catch (JsonProcessingException e) {
            ret = new HashMap<>();
        }
        return CommonMethod.getReturnData(ret);
    }

//    public DataResponse addStudentCourseList(DataRequest dataRequest){
//
//    }
}
