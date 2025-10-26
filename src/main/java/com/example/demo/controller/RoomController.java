package com.example.demo.controller;

import com.example.demo.model.Hotel;
import com.example.demo.model.Room;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.ReservationService; // NEW IMPORT
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // NEW IMPORT
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // NEW IMPORT

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final ReservationService reservationService; // NEW INJECTION

    public RoomController(RoomRepository roomRepo, HotelRepository hotelRepo, ReservationService reservationService) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.reservationService = reservationService; // Assign NEW injection
    }

    @GetMapping("/byHotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable Long hotelId) {
        // The return type of hotel.getRooms() is now a Set, convert it to List
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();
        return new ArrayList<>(hotel.getRooms());
    }

    // NEW ENDPOINT: Filter rooms by date availability
    @GetMapping("/available/{hotelId}")
    public List<Room> getAvailableRoomsByDate(
            @PathVariable Long hotelId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {

        // Convert string dates to LocalDate objects
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        // 1. Get IDs of all rooms booked during this period (from ReservationService)
        Set<Long> bookedRoomIds = reservationService.getBookedRoomIds(checkIn, checkOut);

        // 2. Get all rooms for the requested hotel
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();
        // Uses the eagerly loaded rooms from the Hotel entity (or fetches them if lazy)
        List<Room> allHotelRooms = new ArrayList<>(hotel.getRooms());

        // 3. Filter out booked rooms
        List<Room> availableRooms = allHotelRooms.stream()
                // Room must be marked available AND not be in the booked list
                .filter(room -> room.isAvailable() && !bookedRoomIds.contains(room.getId()))
                .collect(java.util.stream.Collectors.toList());

        return availableRooms;
    }


    @PostMapping
    public Room createRoom(@RequestParam Long hotelId, @RequestBody Room room) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();
        room.setHotel(hotel);

        if (room.getImages() != null) {
            // Note: Updated to handle the Set<Image> type
            room.getImages().forEach(image -> image.setRoom(room));
        }
        return roomRepo.save(room);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomRepo.deleteById(id);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room updated) {
        return roomRepo.findById(id).map(r -> {
            r.setRoomNumber(updated.getRoomNumber());
            r.setType(updated.getType());
            r.setPrice(updated.getPrice());
            r.setAvailable(updated.isAvailable());

            // Note: Updated to handle the Set<Image> type
            r.getImages().clear();
            if (updated.getImages() != null) {
                updated.getImages().forEach(image -> {
                    image.setRoom(r);
                    r.getImages().add(image);
                });
            }

            return roomRepo.save(r);
        }).orElseThrow(() -> new RuntimeException("Room not found with id " + id));
    }
}