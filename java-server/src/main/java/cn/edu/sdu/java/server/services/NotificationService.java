package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Notification;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.NotificationRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.springframework.stereotype.Service;
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
        m.put("releaseTime", s.getReleaseTime());
        m.put("num",s.getNum());
        m.put("title",s.getTitle());
        m.put("notificationId", s.getNotificationId());

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
        Notification n = null;

        if (notificationId != null && notificationId > 0) {
            Optional<Notification> op;
            op = notificationRepository.findByNotificationId(notificationId);   //查询获得实体对象
            if(op.isPresent()) {
                n = op.get();
                notificationRepository.delete(n);    //首先数据库永久删除通知信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getNotificationInfo(DataRequest dataRequest) {
        Integer notificationId = dataRequest.getInteger("notificationId");
        Notification s = null;
        Optional<Notification> op;
        if (notificationId != null) {
            op = notificationRepository.findById(notificationId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromNotification(s)); //这里回传包含学生信息的Map对象
    }


    /**
     * 编辑并保存通知信息
     * @param dataRequest 包含通知信息的请求数据
     * @return DataResponse 包含操作结果的响应
     */
    public DataResponse notificationEditSave(DataRequest dataRequest) {
        // 获取通知 ID
        Integer notificationId = dataRequest.getInteger("notificationId");
        Map<String, Object> form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form, "num");

        Notification n = null;
        boolean isNew = false;

        // 如果提供了通知 ID，则尝试获取对应的实体
        if (notificationId != null) {
            Optional<Notification> op = notificationRepository.findById(notificationId);
            if (op.isPresent()) {
                n = op.get();
            }
        }

        // 检查编号是否已存在
        Optional<Notification> nOp = notificationRepository.findByNum(num);
        if (nOp.isPresent()) {
            // 如果编号已存在，且当前操作不是更新该编号对应的记录，则返回错误
            if (n == null || !n.getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新编号已经存在，不能添加或修改！");
            }
        }


        // 如果没有找到通知 ID 对应的实体，则创建一个新的实体
        if (n == null) {
            n = new Notification();
            isNew = true;
        }

        // 设置表单数据到通知实体
        n.setNotificationId(notificationId);
        n.setNum(num);
        n.setTitle(CommonMethod.getString(form, "title"));
        String t = DateTimeTool.parseDateTime(CommonMethod.getTime(form, "releaseTime"), "yyyy-MM-dd HH:mm:ss");
        System.out.println(t);
        n.setReleaseTime(t);

        // 保存通知实体
        notificationRepository.save(n);


        // 返回结果
        if (isNew) {
            return CommonMethod.getReturnData(n.getNotificationId(), "新增通知成功");
        } else {
            return CommonMethod.getReturnData(n.getNotificationId(), "通知更新成功");
        }
    }
}
