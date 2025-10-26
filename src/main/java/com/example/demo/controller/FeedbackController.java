package com.example.demo.controller;

import com.example.demo.dto.FeedbackRequestDTO;
import com.example.demo.dto.FeedbackResponseDTO;
import com.example.demo.model.Feedback;
import com.example.demo.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map; // ADD THIS IMPORT

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping("/feedbacks")
    public Feedback addFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        return service.saveFeedback(feedbackRequestDTO);
    }

    // ⭐ NEW ENDPOINT: Update feedback comment (Requires ownership or ADMIN)
    @PutMapping("/feedbacks/{id}")
    @PreAuthorize("hasRole('ADMIN') or @feedbackSecurity.isFeedbackOwner(#id)")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newComment = body.get("comment");
        try {
            Feedback updatedFeedback = service.updateFeedbackComment(id, newComment);
            return ResponseEntity.ok(updatedFeedback);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/hotels/{hotelId}/feedbacks")
    @Transactional
    public List<FeedbackResponseDTO> getFeedbacksForHotel(@PathVariable Long hotelId) {
        return service.getFeedbacksByHotelAsDTO(hotelId);
    }

    @DeleteMapping("/feedbacks/{id}")
    @PreAuthorize("hasRole('ADMIN') or @feedbackSecurity.isFeedbackOwner(#id)")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        service.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}