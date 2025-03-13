package com.eventmanagement.service;

import com.eventmanagement.dto.FeedbackDTO;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Feedback;
import com.eventmanagement.model.Registration;
import com.eventmanagement.model.User;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.FeedbackRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public FeedbackDTO.Response submitFeedback(FeedbackDTO.Request request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));
        
        // Check if user attended the event
        boolean attended = registrationRepository.findByUserAndEvent(user, event).stream()
                .anyMatch(registration -> registration.getStatus() == Registration.RegistrationStatus.ATTENDED);
        
        if (!attended) {
            throw new BadRequestException("User must have attended the event to submit feedback");
        }
        
        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setEvent(event);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    public List<FeedbackDTO.Response> getEventFeedback(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<Feedback> feedbacks = feedbackRepository.findByEvent(event);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public double getEventAverageRating(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<Feedback> feedbacks = feedbackRepository.findByEvent(event);
        return feedbacks.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);
    }

    private FeedbackDTO.Response convertToDTO(Feedback feedback) {
        FeedbackDTO.Response dto = new FeedbackDTO.Response();
        dto.setId(feedback.getId());
        dto.setEventId(feedback.getEvent().getId());
        dto.setEventName(feedback.getEvent().getName());
        dto.setUserId(feedback.getUser().getId());
        dto.setUserName(feedback.getUser().getName());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        return dto;
    }
}