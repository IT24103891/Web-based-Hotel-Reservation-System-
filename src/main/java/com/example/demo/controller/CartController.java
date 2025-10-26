package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.HotelService;
import com.example.demo.model.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.HotelServiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemRepository cartRepo;
    private final HotelServiceRepository serviceRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    public CartController(CartItemRepository cartRepo,
                          HotelServiceRepository serviceRepo,
                          UserRepository userRepo,
                          EmailService emailService) {
        this.cartRepo = cartRepo;
        this.serviceRepo = serviceRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    // Add item to cart
    @PostMapping("/add")
    public CartItem addToCart(@RequestParam Long userId,
                              @RequestParam Long serviceId,
                              @RequestParam int quantity) {

        if (!serviceRepo.existsById(serviceId)) {
            throw new IllegalArgumentException("Service does not exist: " + serviceId);
        }

        CartItem item = new CartItem(null, userId, serviceId, quantity);
        return cartRepo.save(item);
    }

    // Get cart items with service details (bulk fetch)
    @GetMapping("/{userId}")
    public List<CartResponse> getUserCart(@PathVariable Long userId) {
        List<CartItem> items = cartRepo.findByUserId(userId);

        // Bulk fetch services
        Map<Long, HotelService> services = serviceRepo.findAllById(
                items.stream().map(CartItem::getServiceId).toList()
        ).stream().collect(Collectors.toMap(HotelService::getId, s -> s));

        return items.stream().map(item -> {
            HotelService s = services.get(item.getServiceId());
            return new CartResponse(
                    item.getId(),
                    s.getId(),
                    s.getName(),
                    s.getDescription(),
                    s.getPrice(),
                    s.getImageUrl(),
                    item.getQuantity()
            );
        }).collect(Collectors.toList());
    }

    // Update quantity of a cart item
    @PutMapping("/update/{id}")
    public CartItem updateCart(@PathVariable Long id, @RequestParam int quantity) {
        return cartRepo.findById(id).map(item -> {
            item.setQuantity(quantity);
            return cartRepo.save(item);
        }).orElseThrow(() -> new RuntimeException("Cart item not found: " + id));
    }

    // Remove single item from cart
    @DeleteMapping("/remove/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartRepo.deleteById(id);
    }

    // Checkout cart (transactional)
    @PostMapping("/checkout/{userId}")
    @Transactional
    public String checkout(@PathVariable Long userId) {
        List<CartItem> items = cartRepo.findByUserId(userId);
        if (items.isEmpty()) {
            return "Cart is empty.";
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Bulk fetch services
        Map<Long, HotelService> services = serviceRepo.findAllById(
                items.stream().map(CartItem::getServiceId).toList()
        ).stream().collect(Collectors.toMap(HotelService::getId, s -> s));

        String serviceList = items.stream()
                .map(i -> {
                    HotelService s = services.get(i.getServiceId());
                    return s.getName() + " x " + i.getQuantity();
                })
                .collect(Collectors.joining(", "));

        String confirmationMessage;

        // ⭐ START OF IMPROVED EMAIL BLOCK ⭐
        try {
            // Send confirmation email
            String body = "Hello " + user.getName() + ",\n\nYour booking is confirmed for: " + serviceList;
            emailService.sendEmail(user.getEmail(), "Booking Confirmation (Hotel Services)", body);
            confirmationMessage = "Checkout complete, confirmation sent to " + user.getEmail();
        } catch (Exception e) {
            // Log the error but DO NOT fail the transaction
            System.err.println("WARNING: Failed to send confirmation email. Check mail server configuration. Error: " + e.getMessage());
            confirmationMessage = "Checkout complete. NOTE: Email confirmation failed. Please check your order details manually.";
        }
        // ⭐ END OF IMPROVED EMAIL BLOCK ⭐

        // Delete all cart items for user (THIS MUST HAPPEN AFTER EMAIL ATTEMPT/FAILURE)
        cartRepo.deleteByUserId(userId);

        return confirmationMessage;
    }

    // DTO for frontend
    public record CartResponse(
            Long cartItemId,
            Long serviceId,
            String name,
            String description,
            double price,
            String imageUrl,
            int quantity
    ) {}
}