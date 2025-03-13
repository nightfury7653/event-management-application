package com.eventmanagement.service;

import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.model.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public List<EventDTO.Response> getUpcomingEvents() {
        List<Event> events = eventRepository.findByEventDateAfterAndStatusOrderByEventDateAsc(
                LocalDateTime.now(), Event.EventStatus.UPCOMING);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO.Response> getEventsByLocation(String location) {
        List<Event> events = eventRepository.findUpcomingEventsByLocation(location, LocalDateTime.now());
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO.Response> getEventsByOrganizer(String organizerId) {
        List<Event> events = eventRepository.findByOrganizerOrderByEventDateDesc(organizerId);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO.Response getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToDTO(event);
    }

    public EventDTO.Response createEvent(EventDTO.Request eventRequest, String organizerId) {
        // Verify organizer exists
        userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + organizerId));
        
        Event event = new Event();
        event.setName(eventRequest.getName());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setLocation(eventRequest.getLocation());
        event.setCapacity(eventRequest.getCapacity());
        event.setTicketPrice(eventRequest.getTicketPrice());
        event.setImageUrl(eventRequest.getImageUrl());
        event.setOrganizer(organizerId);
        event.setStatus(Event.EventStatus.UPCOMING);
        
        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    public EventDTO.Response updateEvent(String id, EventDTO.Request eventRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setName(eventRequest.getName());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setLocation(eventRequest.getLocation());
        event.setCapacity(eventRequest.getCapacity());
        event.setTicketPrice(eventRequest.getTicketPrice());
        event.setImageUrl(eventRequest.getImageUrl());
        
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }

    public void cancelEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setStatus(Event.EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    private EventDTO.Response convertToDTO(Event event) {
        EventDTO.Response dto = new EventDTO.Response();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setCapacity(event.getCapacity());
        dto.setStatus(event.getStatus());
        dto.setOrganizer(event.getOrganizer());
        dto.setImageUrl(event.getImageUrl());
        dto.setTicketPrice(event.getTicketPrice());
        dto.setCreatedAt(event.getCreatedAt());
        
        // Get registration count
        long registrationCount = registrationRepository.countByEventAndStatus(
                event, Registration.RegistrationStatus.CONFIRMED);
        dto.setRegistrationCount((int) registrationCount);
        
        return dto;
    }
}