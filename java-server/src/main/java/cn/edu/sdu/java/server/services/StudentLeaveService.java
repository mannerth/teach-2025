package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentLeave;
import cn.edu.sdu.java.server.repositorys.StudentLeaveRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentLeaveService {
    private final StudentLeaveRepository studentLeaveRepository;

    public StudentLeaveService(StudentLeaveRepository studentLeaveRepository) {
        this.studentLeaveRepository = studentLeaveRepository;
    }

    public Map<String,Object> getMapFromStudentLeave(StudentLeave l) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(l == null)
            return m;
        m.put("name",l.getName());
        m.put("className",l.getClassName());
        m.put("reason",l.getReason());
        m.put("startDate",l.getStartdate());
        m.put("endDate",l.getEnddate());
        p = l.getPerson();
        if(p == null)
            return m;
        m.put("personId",l.getPersonId());
        m.put("name",p.getName());
        return m;
    }


    public List<Map<String,Object>> getStudentLeaveList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<StudentLeave> sList = studentLeaveRepository.findStudentLeaveListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (StudentLeave student : sList) {
            dataList.add(getMapFromStudentLeave(student));
        }
        return dataList;
    }
}
