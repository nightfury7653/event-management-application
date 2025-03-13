package com.eventmanagement.dto;

import lombok.Data;
import com.eventmanagement.model.Notification;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private String id;
    private String message;
    private Notification.NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
}
