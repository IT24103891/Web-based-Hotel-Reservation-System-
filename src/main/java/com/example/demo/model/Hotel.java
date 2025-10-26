package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Data // Keep @Data for getters/setters/toString (but configure hashCode/equals below)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // <-- FIX: Only use fields explicitly marked
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // <-- FIX: Explicitly include ID
    private Long id;

    private String name;
    private String location;
    private String description;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude // Exclude from toString to avoid recursive logging
    @EqualsAndHashCode.Exclude // <-- FIX: Exclude from equals/hashCode
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <-- FIX
    private Set<HotelService> services = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("hotel-feedback")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <-- FIX
    private Set<Feedback> feedbacks = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("hotel-images")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <-- FIX
    private Set<Image> images = new HashSet<>();
}