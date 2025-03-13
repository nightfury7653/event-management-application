package com.eventmanagement.repository;

import com.eventmanagement.model.Notification;
import com.eventmanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserAndReadOrderByCreatedAtDesc(User user, boolean read);
    long countByUserAndRead(User user, boolean read);
}