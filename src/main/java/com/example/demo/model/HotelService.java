package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;

    private String imageUrl; // new field for image
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    @JsonBackReference  // prevents infinite recursion
    private Hotel hotel;
}
