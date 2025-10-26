package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Data // Using Lombok for getters, setters, etc.
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private int rating; // 1 to 5

    // NEW: Link to the User (Many feedbacks to One user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-feedback")
    private User user;

    // NEW: Link to the Hotel (Many feedbacks to One hotel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference("hotel-feedback")
    private Hotel hotel;
}