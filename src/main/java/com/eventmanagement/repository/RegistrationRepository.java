package com.eventmanagement.repository;

import com.eventmanagement.model.Registration;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RegistrationRepository extends MongoRepository<Registration, String> {
    List<Registration> findByUser(User user);
    List<Registration> findByEvent(Event event);
    List<Registration> findByEventAndStatus(Event event, Registration.RegistrationStatus status);
    long countByEventAndStatus(Event event, Registration.RegistrationStatus status);
    boolean existsByUserAndEvent(User user, Event event);
    List<Registration> findByUserAndEvent(User user, Event event);
}