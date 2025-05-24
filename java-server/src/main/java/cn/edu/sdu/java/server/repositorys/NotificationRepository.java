package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Optional<Notification> findByNotificationId(Integer notificationId);
    Optional<Notification> findByNum(String num);
    List<Notification> findByTitle(String title);

    @Query(value = "from Notification where ?1='' or num like %?1% or title like %?1% ")
    List<Notification> findNotificationListByNumTitle(String numTitle);

    @Query(value = "from Notification where ?1='' or num like %?1% or title like %?1% ",
            countQuery = "SELECT count(notificationId) from Notification where ?1='' or num like %?1% or title like %?1% ")
    Page<Notification> findNotificationPageByNumTitle(String numTitle,  Pageable pageable);

}