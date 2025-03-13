package com.eventmanagement.repository;

import com.eventmanagement.model.Feedback;
import com.eventmanagement.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    @Query(value = "{'event': ?0}", fields = "{'rating': 1}")
    List<Feedback> findByEvent(Event event);
}
