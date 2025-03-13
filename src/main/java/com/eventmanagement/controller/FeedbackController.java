package com.eventmanagement.controller;

import com.eventmanagement.dto.FeedbackDTO;
import com.eventmanagement.security.CurrentUser;
import com.eventmanagement.security.UserPrincipal;
import com.eventmanagement.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FeedbackDTO.Response> submitFeedback(
            @Valid @RequestBody FeedbackDTO.Request request,
            @CurrentUser UserPrincipal currentUser) {
        return new ResponseEntity<>(
                feedbackService.submitFeedback(request, currentUser.getId()),
                HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FeedbackDTO.Response>> getEventFeedback(@PathVariable String eventId) {
        return ResponseEntity.ok(feedbackService.getEventFeedback(eventId));
    }

    @GetMapping("/event/{eventId}/rating")
    public ResponseEntity<Map<String, Double>> getEventAverageRating(@PathVariable String eventId) {
        double avgRating = feedbackService.getEventAverageRating(eventId);
        return ResponseEntity.ok(Map.of("averageRating", avgRating));
    }
}