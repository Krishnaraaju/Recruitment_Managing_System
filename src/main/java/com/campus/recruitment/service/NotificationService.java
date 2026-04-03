package com.campus.recruitment.service;

import com.campus.recruitment.dto.response.NotificationResponse;
import com.campus.recruitment.entity.enums.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {
    void createNotification(Long userId, NotificationType type, String message);
    Page<NotificationResponse> getMyNotifications(Long userId, int page, int size);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
}
