package com.eventmanagement.service;

import com.eventmanagement.dto.RegistrationDTO;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Registration;
import com.eventmanagement.model.User;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RegistrationDTO.Response registerForEvent(RegistrationDTO.Request request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));
        
        // Check if event is upcoming
        if (event.getStatus() != Event.EventStatus.UPCOMING) {
            throw new BadRequestException("Cannot register for an event that is not upcoming");
        }
        
        // Check if event has capacity
        long registrationCount = registrationRepository.countByEventAndStatus(event, Registration.RegistrationStatus.CONFIRMED);
        if (registrationCount >= event.getCapacity()) {
            throw new BadRequestException("Event has reached its capacity");
        }
        
        // Check if user is already registered
        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new BadRequestException("User is already registered for this event");
        }
        
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(Registration.RegistrationStatus.PENDING);
        registration.setPaymentId(request.getPaymentId());
        registration.setAmountPaid(event.getTicketPrice());
        
        Registration savedRegistration = registrationRepository.save(registration);
        
        // Send notification
        notificationService.sendNotification(user, 
            "Your registration for " + event.getName() + " has been confirmed", 
            NotificationService.NotificationType.REGISTRATION_CONFIRMATION);
        
        return convertToDTO(savedRegistration);
    }

    public List<RegistrationDTO.Response> getUserRegistrations(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Registration> registrations = registrationRepository.findByUser(user);
        return registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RegistrationDTO.Response> getEventRegistrations(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        List<Registration> registrations = registrationRepository.findByEvent(event);
        return registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RegistrationDTO.Response updateRegistrationStatus(String registrationId, Registration.RegistrationStatus status) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + registrationId));
        
        registration.setStatus(status);
        Registration updatedRegistration = registrationRepository.save(registration);
        
        // Send notification when registration is confirmed
        if (status == Registration.RegistrationStatus.CONFIRMED) {
            notificationService.sendNotification(registration.getUser(), 
                "Your registration for " + registration.getEvent().getName() + " has been confirmed", 
                NotificationService.NotificationType.REGISTRATION_CONFIRMATION);
        }
        
        return convertToDTO(updatedRegistration);
    }

    public void cancelRegistration(String registrationId, String userId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + registrationId));
        
        if (!registration.getUser().getId().equals(userId)) {
            throw new BadRequestException("User can only cancel their own registrations");
        }
        
        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }

    private RegistrationDTO.Response convertToDTO(Registration registration) {
        RegistrationDTO.Response dto = new RegistrationDTO.Response();
        dto.setId(registration.getId());
        dto.setEventId(registration.getEvent().getId());
        dto.setEventName(registration.getEvent().getName());
        dto.setUserId(registration.getUser().getId());
        dto.setUserName(registration.getUser().getName());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setStatus(registration.getStatus());
        dto.setPaymentId(registration.getPaymentId());
        dto.setAmountPaid(registration.getAmountPaid());
        return dto;
    }
}