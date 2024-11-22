package com.gamegoo.repository.notification;

import com.gamegoo.domain.notification.NotificationType;
import com.gamegoo.domain.notification.NotificationTypeTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findNotificationTypeByTitle(NotificationTypeTitle title);

}
