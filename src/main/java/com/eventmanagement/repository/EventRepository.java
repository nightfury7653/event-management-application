package com.eventmanagement.repository;

import com.eventmanagement.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByEventDateAfterAndStatusOrderByEventDateAsc(LocalDateTime now, Event.EventStatus status);
    List<Event> findByOrganizerOrderByEventDateDesc(String organizer);
    
    @Query("{'location': ?0, 'eventDate': {$gte: ?1}}")
    List<Event> findUpcomingEventsByLocation(String location, LocalDateTime now);
}