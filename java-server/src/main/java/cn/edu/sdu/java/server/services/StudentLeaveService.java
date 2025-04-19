package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.StudentLeaveRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentLeaveService {
    private final StudentLeaveRepository studentLeaveRepository;
//    private final PersonRepository personLeaveRepository;  //人员数据操作自动注入
//    private final UserRepository userLeaveRepository;  //学生数据操作自动注入

    public StudentLeaveService(StudentLeaveRepository studentLeaveRepository, PersonRepository personRepository, UserRepository userRepository) {
        this.studentLeaveRepository = studentLeaveRepository;
//        this.personLeaveRepository = personRepository;
//        this.userLeaveRepository = userRepository;
    }

    public Map<String,Object> getMapFromStudentLeave(StudentLeave l) {
        Map<String,Object> m = new HashMap<>();
        if(l == null)
            return m;
        m.put("leaveId",l.getLeaveId());
        m.put("name",l.getName());
        m.put("className",l.getClassName());
        m.put("reason",l.getReason());
        m.put("startDate",l.getStartdate());
        m.put("endDate",l.getEnddate());

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


    public DataResponse studentLeaveDelete(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");  //获取student_id值
        StudentLeave s = null;
        Optional<StudentLeave> op;
        if (leaveId != null && leaveId > 0) {
            op = studentLeaveRepository.findById(leaveId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                studentLeaveRepository.delete(s);    //首先数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getStudentLeaveInfo(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        StudentLeave s = null;
        Optional<StudentLeave> op;
        if (leaveId != null) {
            op = studentLeaveRepository.findById(leaveId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromStudentLeave(s)); //这里回传包含学生信息的Map对象
    }


    public DataResponse studentLeaveEditSave(DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Map<String,Object> form = dataRequest.getMap("form"); //参数获取Map对象
        System.out.println(form);

        StudentLeave s = null;
//        StudentLeave s = new StudentLeave();
        boolean isNew = false;

        Optional<StudentLeave> op;

        if (leaveId != null) {
            op = studentLeaveRepository.findById(leaveId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }


        // 如果没有找到通知 ID 对应的实体，则创建一个新的实体
        if (s == null) {
            s = new StudentLeave();
            isNew = true;
        }
        studentLeaveRepository.save(s);
        System.out.println(s.getLeaveId());
        s.setName(CommonMethod.getString(form, "name"));
        System.out.println(CommonMethod.getString(form, "name"));
        s.setClassName(CommonMethod.getString(form, "className"));
        s.setReason(CommonMethod.getString(form, "reason"));
        s.setStartdate(CommonMethod.getDate(form, "startDate"));
        s.setEnddate(CommonMethod.getDate(form, "endDate"));

        studentLeaveRepository.save(s);  //修改保存学生信息

        System.out.println(s.getName());
        System.out.println(s.getReason());
//        systemService.modifyLog(s,isNew);
        return CommonMethod.getReturnData(s.getLeaveId());  // 将personId返回前端

    }

}
