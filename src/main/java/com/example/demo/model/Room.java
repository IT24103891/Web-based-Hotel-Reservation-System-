package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data // Keep @Data for getters/setters/toString (but configure hashCode/equals below)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // <-- FIX: Only use fields explicitly marked
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // <-- FIX: Explicitly include ID
    private Long id;

    private String roomNumber;
    private String type;
    private double price;
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <-- FIX: Exclude parent reference
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("room-images")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <-- FIX: Exclude collection
    private Set<Image> images = new HashSet<>();
}