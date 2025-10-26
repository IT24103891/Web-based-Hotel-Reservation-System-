package com.example.demo.controller;

import com.example.demo.model.Hotel;
import com.example.demo.model.HotelService;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.HotelServiceRepository;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelRepository hotelRepo;
    private final HotelServiceRepository hotelServiceRepo;
    private final FeedbackRepository feedbackRepo;

    public HotelController(HotelRepository hotelRepo, HotelServiceRepository hotelServiceRepo, FeedbackRepository feedbackRepo) {
        this.hotelRepo = hotelRepo;
        this.hotelServiceRepo = hotelServiceRepo;
        this.feedbackRepo = feedbackRepo;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAllWithRoomsAndImages();
    }

    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelRepo.findByIdWithRoomsAndImages(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }

    @GetMapping("/with-ratings")
    public List<Map<String, Object>> getAllHotelsWithRatings() {
        return hotelRepo.findAll().stream().map(hotel -> {
            Double avgRating = feedbackRepo.findAverageRatingByHotelId(hotel.getId());

            Map<String, Object> hotelMap = new java.util.HashMap<>();
            hotelMap.put("id", hotel.getId());
            hotelMap.put("name", hotel.getName());
            hotelMap.put("location", hotel.getLocation());
            hotelMap.put("description", hotel.getDescription());
            hotelMap.put("rooms", hotel.getRooms());
            hotelMap.put("services", hotel.getServices());
            hotelMap.put("feedbacks", hotel.getFeedbacks());
            hotelMap.put("images", hotel.getImages());

            hotelMap.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);

            return hotelMap;
        }).collect(Collectors.toList());
    }

    @PostMapping
    public Hotel createHotel(@RequestBody Hotel hotel) {
        if (hotel.getImages() != null) {
            hotel.getImages().forEach(image -> image.setHotel(hotel));
        }
        return hotelRepo.save(hotel);
    }

    @PutMapping("/{id}")
    public Hotel updateHotel(@PathVariable Long id, @RequestBody Hotel updated) {
        return hotelRepo.findById(id).map(h -> {
            h.setName(updated.getName());
            h.setLocation(updated.getLocation());
            h.setDescription(updated.getDescription());

            h.getImages().clear();
            if (updated.getImages() != null) {
                updated.getImages().forEach(image -> {
                    image.setHotel(h);
                    h.getImages().add(image);
                });
            }

            return hotelRepo.save(h);
        }).orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable Long id) {
        hotelRepo.deleteById(id);
    }

    @GetMapping("/{hotelId}/services")
    public List<HotelService> getServicesByHotelId(@PathVariable Long hotelId) {
        // This findById does NOT use eager fetching, so it may still cause a
        // LazyInitializationException if services is accessed outside a transaction.
        // For a full fix, you might need to adjust the Service model or repository as well.
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id " + hotelId));
        return hotelServiceRepo.findByHotel(hotel);
    }
}