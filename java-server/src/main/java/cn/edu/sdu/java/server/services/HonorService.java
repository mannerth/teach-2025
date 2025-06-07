package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.EHonorType;
import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.HonorType;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.HonorTypeRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HonorService {

    final private HonorRepository honorRepository;
    final private StudentRepository studentRepository;
    final private HonorTypeRepository honorTypeRepository;

    public HonorService(HonorRepository honorRepository, StudentRepository studentRepository, HonorTypeRepository honorTypeRepository) {
        this.honorRepository = honorRepository;
        this.studentRepository = studentRepository;
        this.honorTypeRepository = honorTypeRepository;
    }

    /// 获取学生的所有荣誉
    public DataResponse getHonors(DataRequest req){
        //更改：personId
        Integer personId = req.getInteger("personId");
        Optional<Student> _s = studentRepository.findByPersonPersonId(personId);
        if(_s.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息! ");
        }
        Student student = _s.get();
        List<Honor> honors = honorRepository.findByStudentId(student.getPersonId());
        List<Map> dataList = new ArrayList<>();
        for(Honor h : honors){
            dataList.add(getMapFromHonor(h));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getHonorCount(DataRequest req){
        return CommonMethod.getReturnData(honorRepository.count());
    }

    public DataResponse getHonorList(DataRequest req){
        Map form = req.getMap("form");
        String numName = CommonMethod.getString(form,"numName");
        String honorType = CommonMethod.getString(form,"honorType");
        EHonorType eHonorType = EHonorType.fromString(honorType);
        Integer pageIndex = req.getInteger("page");
        Integer pageSize = req.getInteger("pageSize");

        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        System.out.println(numName);
        List<Honor> honorList = honorRepository.findByNumNameAndType(numName,eHonorType,pageable);
        List<Map> dataList = new ArrayList<>();
        for(Honor h : honorList){
            dataList.add(getMapFromHonor(h));
        }
        Map data = new HashMap<>();
        data.put("honorData", dataList);
        data.put("count",honorRepository.countByNumNameAndType(numName,eHonorType));
        return CommonMethod.getReturnData(data);
    }

    public DataResponse deleteHonor(DataRequest req){
        Integer honorId = req.getInteger("honorId");
        if(honorId == null){
            return CommonMethod.getReturnMessageError("无法获取荣誉信息");
        }
        Optional<Honor> hOp = honorRepository.findById(honorId);
        if (hOp.isEmpty())
        {
            return CommonMethod.getReturnMessageError("无法获取荣誉信息");
        }
        Honor honor = hOp.get();
        honorRepository.delete(honor);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse saveHonor(DataRequest req){
        Integer honorId = req.getInteger("honorId");
        String honorContent = req.getString("honorContent");
        String honorType = req.getString("honorType");
        Integer personId = req.getInteger("personId");
        Optional<Student> _s = studentRepository.findByPersonPersonId(personId);
        if(_s.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息! ");
        }
        Student student = _s.get();
        Honor honor = null;
        if(honorId != null){
            Optional<Honor> hOp = honorRepository.findById(honorId);
            if(hOp.isPresent()){
                honor = hOp.get();
            }
        }
        if(honor == null){
            honor = new Honor();
        }

        Optional<HonorType> tOp = honorTypeRepository.findByEHonorType(EHonorType.fromString(honorType));
        if(tOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取荣誉类型信息!");
        }
        HonorType type = tOp.get();

        honor.setHonorContent(honorContent);
        honor.setHonorType(type);
        honor.setStudent(student);
        honorRepository.save(honor);
        return CommonMethod.getReturnMessageOK();
    }

    private Map getMapFromHonor(Honor h){
        if(h == null){
            return null;
        }
        Map m = new HashMap<>();
        m.put("studentId",h.getStudent().getPersonId());
        m.put("studentName",h.getStudent().getPerson().getName());
        m.put("studentNum",h.getStudent().getPerson().getNum());
        m.put("honorType",h.getHonorType().getType().toString());
        m.put("honorContent",h.getHonorContent());
        m.put("honorId",h.getHonorId());
        return m;
    }
}
