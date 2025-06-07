package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.*;

@Service
public class StudentRegisterService {
    private static final Logger log = LoggerFactory.getLogger(StudentRegisterService.class);
    private final PersonRepository personRepository;
    private final StudentRegisterRepository studentRegisterRepository;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder encoder;
    private final FeeRepository feeRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final SystemService systemService;

    public StudentRegisterService(PersonRepository personRepository, StudentRegisterRepository studentRegisterRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder, FeeRepository feeRepository, FamilyMemberRepository familyMemberRepository, SystemService systemService) {
        this.personRepository = personRepository;
        this.studentRegisterRepository = studentRegisterRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
        this.feeRepository = feeRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.systemService = systemService;
    }

    public Map<String, Object> getMapFromStudentRegister(StudentRegister s) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (s == null)
            return m;
        m.put("major", s.getMajor());
        m.put("studentName", s.getStudentName());
        m.put("studentNumber", s.getStudentNumber());
        m.put("className", s.getClassName());
        m.put("registrationLocation", s.getRegistrationLocation());
        m.put("registrationTime", s.getRegistrationTime());
        m.put("subject", s.getSubject());
        p = s.getPerson();
        if (p == null)
            return m;
        m.put("personId", s.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        String gender = p.getGender();
        m.put("gender", gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender));
        m.put("birthday", p.getBirthday());
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.put("address", p.getAddress());
        return m;
    }

    public List<Map<String, Object>> getStudentRegisterMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<StudentRegister> sList = studentRegisterRepository.findStudentListByNumName(numName);
        if (sList == null || sList.isEmpty())
            return dataList;
        for (StudentRegister student : sList) {
            dataList.add(getMapFromStudentRegister(student));
        }
        return dataList;
    }

    public DataResponse getStudentRegisterList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getStudentRegisterMapList(numName);
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse studentRegisterDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Optional<StudentRegister> op = studentRegisterRepository.findById(personId);
        if (op.isPresent()) {
            StudentRegister s = op.get();
            Optional<User> uOp = userRepository.findById(personId);
            uOp.ifPresent(userRepository::delete);
            Person p = s.getPerson();
            studentRegisterRepository.delete(s);
            personRepository.delete(p);
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getStudentRegisterInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Optional<StudentRegister> op = studentRegisterRepository.findById(personId);
        StudentRegister s = op.orElse(null);
        return CommonMethod.getReturnData(getMapFromStudentRegister(s));
    }

    public DataResponse studentRegisterEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map<String, Object> form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form, "num");
        StudentRegister s = null;
        Person p;
        User u;
        boolean isNew = false;
        if (personId != null) {
            Optional<StudentRegister> op = studentRegisterRepository.findById(personId);
            s = op.orElse(null);
        }
        Optional<Person> nOp = personRepository.findByNum(num);
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("1");
            personRepository.saveAndFlush(p);
            personId = p.getPersonId();
            String password = encoder.encode("123456");
            u = new User();
            u.setPersonId(personId);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT.name()));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getPersonId());
            userRepository.saveAndFlush(u);
            s = new StudentRegister();
            s.setPersonId(personId);
            studentRegisterRepository.saveAndFlush(s);
            isNew = true;
        } else {
            p = s.getPerson();
        }
        if (!num.equals(p.getNum())) {
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            uOp.ifPresent(user -> {
                user.setUserName(num);
                userRepository.saveAndFlush(user);
            });
            p.setNum(num);
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);
        s.setMajor(CommonMethod.getString(form, "major"));
        s.setStudentName(CommonMethod.getString(form, "studentName"));
        s.setStudentNumber(CommonMethod.getString(form, "studentNumber"));
        s.setClassName(CommonMethod.getString(form, "className"));
        s.setRegistrationLocation(CommonMethod.getString(form, "registrationLocation"));
        s.setRegistrationTime(CommonMethod.getString(form, "registrationTime"));
        s.setSubject(CommonMethod.getString(form, "subject"));
        studentRegisterRepository.save(s);
        systemService.modifyLog(s, isNew);
        return CommonMethod.getReturnData(s.getPersonId());
    }

    public ResponseEntity<StreamingResponseBody> getStudentRegisterListExcl(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getStudentRegisterMapList(numName);
        Integer[] widths = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30, 20, 20, 20, 20, 20};
        String[] titles = {"序号", "学号", "姓名", "学院", "专业", "班级", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址", "学生姓名", "学号", "注册地点", "注册时间", "科目"};
        String outPutSheetName = "student_register.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (int j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = sheet.createRow(0);
        XSSFCell[] cell = new XSSFCell[widths.length];
        for (int j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
        }
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                Map<String, Object> m = list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "major"));
                cell[5].setCellValue(CommonMethod.getString(m, "className"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
                cell[12].setCellValue(CommonMethod.getString(m, "studentName"));
                cell[13].setCellValue(CommonMethod.getString(m, "studentNumber"));
                cell[14].setCellValue(CommonMethod.getString(m, "registrationLocation"));
                cell[15].setCellValue(CommonMethod.getString(m, "registrationTime"));
                cell[16].setCellValue(CommonMethod.getString(m, "subject"));
            }
        }
        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public DataResponse getStudentRegisterPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int size = 40;
        List<Map<String, Object>> dataList = new ArrayList<>();
        Pageable pageable = PageRequest.of(cPage, size);
        Page<StudentRegister> page = studentRegisterRepository.findStudentPageByNumName(numName, pageable);
        if (page != null) {
            int dataTotal = (int) page.getTotalElements();
            List<StudentRegister> list = page.getContent();
            for (StudentRegister student : list) {
                dataList.add(getMapFromStudentRegister(student));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("dataTotal", dataTotal);
            data.put("pageSize", size);
            data.put("dataList", dataList);
            return CommonMethod.getReturnData(data);
        }
        return CommonMethod.getReturnData(new HashMap<>());
    }

    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        List<FamilyMember> fList = familyMemberRepository.findByStudentPersonId(personId);
        List<Map<String, Object>> dataList = new ArrayList<>();
        if (fList != null) {
            for (FamilyMember f : fList) {
                Map<String, Object> m = new HashMap<>();
                m.put("memberId", f.getMemberId());
                m.put("personId", f.getStudent().getPersonId());
                m.put("relation", f.getRelation());
                m.put("name", f.getName());
                m.put("gender", f.getGender());
                m.put("age", f.getAge() + "");
                m.put("unit", f.getUnit());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse familyMemberSave(DataRequest dataRequest) {
        Map<String, Object> form = dataRequest.getMap("form");
        Integer personId = CommonMethod.getInteger(form, "personId");
        Integer memberId = CommonMethod.getInteger(form, "memberId");
        FamilyMember f = null;
        if (memberId != null) {
            Optional<FamilyMember> op = familyMemberRepository.findById(memberId);
            f = op.orElse(null);
        }
        if (f == null) {
            f = new FamilyMember();
            //f.setStudent(studentRegisterRepository.findById(personId).get());
        }
        f.setRelation(CommonMethod.getString(form, "relation"));
        f.setName(CommonMethod.getString(form, "name"));
        f.setGender(CommonMethod.getString(form, "gender"));
        f.setAge(CommonMethod.getInteger(form, "age"));
        f.setUnit(CommonMethod.getString(form, "unit"));
        familyMemberRepository.save(f);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse familyMemberDelete(DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<FamilyMember> op = familyMemberRepository.findById(memberId);
        op.ifPresent(familyMemberRepository::delete);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse importFeeDataWeb(Map<String, Object> request, MultipartFile file) {
        Integer personId = CommonMethod.getInteger(request, "personId");
        try {
            String msg = importFeeData(personId, file.getInputStream());
            if (msg == null)
                return CommonMethod.getReturnMessageOK();
            else
                return CommonMethod.getReturnMessageError(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonMethod.getReturnMessageError("上传错误！");
    }

    private String importFeeData(Integer personId, InputStream in) {
        try {
            StudentRegister student = studentRegisterRepository.findById(personId).get();
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell cell = row.getCell(0);
                if (cell == null)
                    break;
                String day = cell.getStringCellValue();
                cell = row.getCell(1);
                String money = cell.getStringCellValue();
                Optional<Fee> fOp = feeRepository.findByStudentPersonIdAndDay(personId, day);
                Fee f = fOp.orElseGet(() -> {
                    Fee newFee = new Fee();
                    newFee.setDay(day);
                    //newFee.setStudent(student);
                    return newFee;
                });
                double dMoney = money != null && !money.isEmpty() ? Double.parseDouble(money) : 0d;
                f.setMoney(dMoney);
                feeRepository.save(f);
            }
            workbook.close();
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "上传错误！";
        }
    }
}
