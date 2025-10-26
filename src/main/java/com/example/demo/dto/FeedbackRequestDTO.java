package com.example.demo.dto;

import lombok.Data;

@Data
public class FeedbackRequestDTO { // <-- ONLY this class should be in this file
    private String comment;
    private int rating;
    private Long userId;
    private Long hotelId;
}