package com.eventmanagement.service;

import com.eventmanagement.dto.NotificationDTO;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Notification;
import com.eventmanagement.model.Registration;
import com.eventmanagement.model.User;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.NotificationRepository;
import com.eventmanagement.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public enum NotificationType {
        EVENT_REMINDER, REGISTRATION_CONFIRMATION, EVENT_CANCELLED, PAYMENT_CONFIRMATION
    }

    public void sendNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        switch (type) {
            case EVENT_REMINDER:
                notification.setType(Notification.NotificationType.EVENT_REMINDER);
                break;
            case REGISTRATION_CONFIRMATION:
                notification.setType(Notification.NotificationType.REGISTRATION_CONFIRMATION);
                break;
            case EVENT_CANCELLED:
                notification.setType(Notification.NotificationType.EVENT_CANCELLED);
                break;
            case PAYMENT_CONFIRMATION:
                notification.setType(Notification.NotificationType.PAYMENT_CONFIRMATION);
                break;
        }
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getUserNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, false);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndRead(user, false);
    }

    // Scheduled job to send event reminders 24 hours before event
    @Scheduled(cron = "0 0 12 * * ?") // Run at 12:00 PM every day
    public void sendEventReminders() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);
        
        List<Event> upcomingEvents = eventRepository.findByEventDateAfterAndStatusOrderByEventDateAsc(
                startTime, Event.EventStatus.UPCOMING)
                .stream()
                .filter(event -> event.getEventDate().isBefore(endTime))
                .collect(Collectors.toList());
        
        for (Event event : upcomingEvents) {
            List<Registration> registrations = registrationRepository.findByEventAndStatus(
                    event, Registration.RegistrationStatus.CONFIRMED);
            
            for (Registration registration : registrations) {
                sendNotification(registration.getUser(),
                        "Reminder: " + event.getName() + " is happening tomorrow at " + event.getLocation(),
                        NotificationType.EVENT_REMINDER);
            }
        }
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}