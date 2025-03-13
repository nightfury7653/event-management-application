package com.eventmanagement.controller;

import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.security.CurrentUser;
import com.eventmanagement.security.UserPrincipal;
import com.eventmanagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO.Response>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<EventDTO.Response>> getEventsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(eventService.getEventsByLocation(location));
    }

    @GetMapping("/organizer")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<EventDTO.Response>> getOrganizerEvents(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO.Response> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<EventDTO.Response> createEvent(
            @Valid @RequestBody EventDTO.Request eventRequest,
            @CurrentUser UserPrincipal currentUser) {
        return new ResponseEntity<>(
                eventService.createEvent(eventRequest, currentUser.getId()),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<EventDTO.Response> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody EventDTO.Request eventRequest) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelEvent(@PathVariable String id) {
        eventService.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }
}