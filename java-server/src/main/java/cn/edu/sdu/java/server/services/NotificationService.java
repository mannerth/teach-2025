package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.models.Notification;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.NotificationRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import cn.edu.sdu.java.server.payload.request.DataRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Map<String,Object> getMapFromNotification(Notification s) {
        Map<String,Object> m = new HashMap<>();
        if(s == null)
            return m;
        m.put("releaseTime", ZonedDateTime.ofInstant(s.getReleaseTime().toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        m.put("num",s.getNum());
        m.put("title",s.getTitle());

        return m;
    }


    public List<Map<String,Object>> getNotificationMapList(String numTitle) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Notification> sList = notificationRepository.findNotificationListByNumTitle(numTitle);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Notification notification : sList) {
            dataList.add(getMapFromNotification(notification));
        }
        return dataList;
    }

    public DataResponse getNotificationList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getNotificationMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse notificationDelete(DataRequest dataRequest) {
        Integer notificationId = dataRequest.getInteger("notificationId");  //获取notification_id值
        Notification s = null;
        Optional<Notification> op;
        if (notificationId != null && notificationId > 0) {
            op = notificationRepository.findById(notificationId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                notificationRepository.delete(s);    //首先数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getNotificationInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Notification s = null;
        Optional<Notification> op;
        if (personId != null) {
            op = notificationRepository.findById(personId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromNotification(s)); //这里回传包含学生信息的Map对象
    }


    /**
     * 编辑并保存通知信息
     *
     * @param dataRequest 包含通知信息的请求数据
     * @return DataResponse 包含操作结果的响应
     */
    public DataResponse notificationEditSave(DataRequest dataRequest) {
        // 获取通知 ID
        Integer notificationId = dataRequest.getInteger("notificationId");
        if (notificationId == null || notificationId <= 0) {
            return CommonMethod.getReturnMessageError("Invalid notificationId");
        }

        // 获取通知的其他字段
        String title = dataRequest.getString("title");
        Date releaseTime = dataRequest.getDate("release_Time");

        // 检查必填字段是否为空
        if (title == null || title.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("Title cannot be empty");
        }

        // 根据 ID 查询通知
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            return CommonMethod.getReturnMessageError("Notification not found");
        }

        // 获取通知实体并更新字段
        Notification notification = optionalNotification.get();
        notification.setTitle(title);
        notification.setReleaseTime(releaseTime);

        try {
            // 保存更新后的通知
            notificationRepository.save(notification);
            return CommonMethod.getReturnMessageOK("Notification updated successfully");
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            return CommonMethod.getReturnMessageError("Failed to update notification: " + e.getMessage());
        }
    }
}
