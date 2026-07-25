package com.jpcode.screentrack.review;

import com.jpcode.screentrack.review.dto.CreateReviewRequestDto;
import com.jpcode.screentrack.review.dto.ReviewResponseDto;
import com.jpcode.screentrack.review.dto.UpdateReviewRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(@RequestBody @Valid CreateReviewRequestDto dto) {
       return ResponseEntity
               .status(201)
               .body(reviewService.create(dto));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(@PathVariable Long reviewId, @RequestBody @Valid UpdateReviewRequestDto dto) {
        return ResponseEntity.ok(
                reviewService.update(reviewId, dto)
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(
                reviewService.getById(reviewId)
        );
    }
    
    @GetMapping("/media/{imdbId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewByMediaId(@PathVariable String imdbId) {
        return ResponseEntity.ok(
                reviewService.findByMedia(imdbId)
        );
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
        return ResponseEntity.ok(
                reviewService.findMyReviews()
        );
    }

    @DeleteMapping("/my-reviews/{reviewId}")
    public ResponseEntity<Void> deleteOwnReview(@PathVariable Long reviewId) {
        reviewService.deleteOwnReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
