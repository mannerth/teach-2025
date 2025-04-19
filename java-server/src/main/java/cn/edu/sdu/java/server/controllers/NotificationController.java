package cn.edu.sdu.java.server.controllers;import cn.edu.sdu.java.server.payload.request.DataRequest;

import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * NotificationController 主要是为通知管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notification")

public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * getNotificationList 通知管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储通知信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     */


    @PostMapping("/getNotificationList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getNotificationList(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationList(dataRequest);
    }


    /**
     * notificationDelete 删除通知信息Web服务 Notification页面的列表里点击删除按钮则可以删除已经存在的通知信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * @return 正常操作
     */

    @PostMapping("/notificationDelete")
    public DataResponse notificationDelete(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.notificationDelete(dataRequest);
    }

    /**
     * getNotificationInfo 前端点击通知列表时前端获取通知详细信息请求服务
     */

    @PostMapping("/getNotificationInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getNotificationInfo(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.getNotificationInfo(dataRequest);
    }

    /**
     * notificationEditSave 前端通知信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * @return 新建修改通知的主键 notification_id 返回前端
     */
    @PostMapping("/notificationEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse notificationEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return notificationService.notificationEditSave(dataRequest);
    }
}

