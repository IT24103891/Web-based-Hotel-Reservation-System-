package com.example.demo.dto; // Must be in the correct package

import com.example.demo.model.Feedback;
import lombok.Data;

@Data
public class FeedbackResponseDTO { // <-- File name must match class name
    private Long id;
    private String comment;
    private int rating;
    private Long hotelId;
    private Long userId;
    private String reviewerName;

    public FeedbackResponseDTO(Feedback feedback, String reviewerName) {
        this.id = feedback.getId();
        this.comment = feedback.getComment();
        this.rating = feedback.getRating();
        this.hotelId = feedback.getHotel().getId();
        this.userId = feedback.getUser().getId();
        this.reviewerName = reviewerName;
    }
}