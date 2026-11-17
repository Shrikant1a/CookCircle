package com.cookcircle.service;

import com.cookcircle.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto sendNotification(Long userId, String title, String message, String type);
    List<NotificationDto> getUserNotifications(Long userId, Boolean unreadOnly);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
}

