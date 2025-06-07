package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/studentRegister")
public class StudentRegisterController {

    @Autowired
    private StudentRegisterService studentRegisterService;

    /**
     * 获取学生注册信息列表
     * @param dataRequest 请求数据
     * @return 学生注册信息列表响应
     */
    @PostMapping("/list")
    public DataResponse getStudentRegisterList(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.getStudentRegisterList(dataRequest);
    }

    /**
     * 删除学生注册信息
     * @param dataRequest 请求数据
     * @return 删除操作结果响应
     */
    @PostMapping("/delete")
    public DataResponse studentRegisterDelete(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.studentRegisterDelete(dataRequest);
    }

    /**
     * 获取学生注册信息详情
     * @param dataRequest 请求数据
     * @return 学生注册信息详情响应
     */
    @PostMapping("/info")
    public DataResponse getStudentRegisterInfo(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.getStudentRegisterInfo(dataRequest);
    }

    /**
     * 编辑或保存学生注册信息
     * @param dataRequest 请求数据
     * @return 操作结果响应
     */
    @PostMapping("/editSave")
    public DataResponse studentRegisterEditSave(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.studentRegisterEditSave(dataRequest);
    }

    /**
     * 导出学生注册信息列表到 Excel
     * @param dataRequest 请求数据
     * @return Excel 文件流响应
     */
    @PostMapping("/exportExcel")
    public ResponseEntity<?> getStudentRegisterListExcl(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.getStudentRegisterListExcl(dataRequest);
    }

    /**
     * 获取学生注册信息分页数据
     * @param dataRequest 请求数据
     * @return 分页数据响应
     */
    @PostMapping("/pageData")
    public DataResponse getStudentRegisterPageData(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.getStudentRegisterPageData(dataRequest);
    }

    /**
     * 获取学生家庭成员列表
     * @param dataRequest 请求数据
     * @return 家庭成员列表响应
     */
    @PostMapping("/familyMember/list")
    public DataResponse getFamilyMemberList(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.getFamilyMemberList(dataRequest);
    }

    /**
     * 保存学生家庭成员信息
     * @param dataRequest 请求数据
     * @return 保存操作结果响应
     */
    @PostMapping("/familyMember/save")
    public DataResponse familyMemberSave(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.familyMemberSave(dataRequest);
    }

    /**
     * 删除学生家庭成员信息
     * @param dataRequest 请求数据
     * @return 删除操作结果响应
     */
    @PostMapping("/familyMember/delete")
    public DataResponse familyMemberDelete(@RequestBody DataRequest dataRequest) {
        return studentRegisterService.familyMemberDelete(dataRequest);
    }

    /**
     * 导入学生费用数据
     * @param request 请求参数
     * @param file 上传的文件
     * @return 导入操作结果响应
     * @throws IOException 文件操作异常
     */
    @PostMapping("/importFeeData")
    public DataResponse importFeeDataWeb(@RequestParam Map<String, Object> request, @RequestParam("file") MultipartFile file) throws IOException {
        return studentRegisterService.importFeeDataWeb(request, file);
    }
}
