package cn.edu.sdu.java.server.controllers;


import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.HonorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {

    final private HonorService honorService;

    public HonorController(HonorService honorService) {
        this.honorService = honorService;
    }

    //获取学生的所有荣誉
    @PostMapping("/getHonors")
    public DataResponse getHonors(@Valid @RequestBody DataRequest req){
        return honorService.getHonors(req);
    }

    //获取荣誉个数
    @PostMapping("/getHonorCount")
    public DataResponse getHonorCount(@Valid @RequestBody DataRequest req){
        return honorService.getHonorCount(req);
    }

    //获取荣誉列表
    @PostMapping("/getHonorList")
    public DataResponse getHonorList(@Valid @RequestBody DataRequest req){
        return honorService.getHonorList(req);
    }

    //删除荣誉
    @PostMapping("/deleteHonor")
    public DataResponse deleteHonor(@Valid @RequestBody DataRequest req){
        return honorService.deleteHonor(req);
    }

    //保存荣誉
    @PostMapping("/saveHonor")
    public DataResponse saveHonor(@Valid @RequestBody DataRequest req){
        return honorService.saveHonor(req);
    }
}
