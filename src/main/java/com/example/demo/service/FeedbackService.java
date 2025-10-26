package com.example.demo.service;

import com.example.demo.dto.FeedbackRequestDTO;
import com.example.demo.dto.FeedbackResponseDTO;
import com.example.demo.model.Feedback;
import com.example.demo.model.Hotel;
import com.example.demo.model.User;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository, HotelRepository hotelRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
    }

    public Feedback saveFeedback(FeedbackRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Feedback feedback = new Feedback();
        feedback.setComment(dto.getComment());
        feedback.setRating(dto.getRating());
        feedback.setUser(user);
        feedback.setHotel(hotel);

        return feedbackRepository.save(feedback);
    }

    // ⭐ NEW METHOD: Update only the comment text
    public Feedback updateFeedbackComment(Long id, String newComment) {
        return feedbackRepository.findById(id).map(feedback -> {
            feedback.setComment(newComment);
            // Keep rating and links unchanged
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new RuntimeException("Feedback not found with id " + id));
    }

    public List<FeedbackResponseDTO> getFeedbacksByHotelAsDTO(Long hotelId) {
        List<Feedback> feedbacks = feedbackRepository.findByHotelId(hotelId);

        return feedbacks.stream()
                .map(f -> new FeedbackResponseDTO(
                        f,
                        f.getUser().getName()
                ))
                .collect(Collectors.toList());
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}