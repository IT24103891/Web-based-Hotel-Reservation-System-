package com.example.demo.controller;

import com.example.demo.model.HotelService;
import com.example.demo.repository.HotelServiceRepository;
import com.example.demo.model.Hotel;
import com.example.demo.repository.HotelRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class HotelServiceController {

    private final HotelServiceRepository repository;
    private final HotelRepository hotelRepository;

    // Update the constructor to inject HotelRepository
    public HotelServiceController(HotelServiceRepository repository, HotelRepository hotelRepository) {
        this.repository = repository;
        this.hotelRepository = hotelRepository;
    }

    @GetMapping
    public List<HotelService> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HotelService getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    @PostMapping
    public HotelService create(@RequestBody HotelService service) {
        // Spring Data JPA automatically handles the 'hotel' object if only the ID is set in the JSON body.
        return repository.save(service);
    }

    @PutMapping("/{id}")
    public HotelService update(@PathVariable Long id, @RequestBody HotelService updated) {
        return repository.findById(id).map(s -> {
            s.setName(updated.getName());
            s.setDescription(updated.getDescription());
            s.setPrice(updated.getPrice());
            s.setImageUrl(updated.getImageUrl());
            // NEW: Crucial to update the hotel link if it was provided in the request body
            if (updated.getHotel() != null) {
                s.setHotel(updated.getHotel());
            }
            return repository.save(s);
        }).orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}