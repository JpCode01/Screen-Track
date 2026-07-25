package com.jpcode.screentrack.feed;

import com.jpcode.screentrack.feed.dto.FeedReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;


    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<FeedReviewResponseDto>> getReviews(Pageable pageable) {
        return ResponseEntity.ok(
                feedService.findReviews(pageable)
        );
    }
}
