package com.jpcode.screentrack.feed;

import com.jpcode.screentrack.feed.dto.FeedReviewResponseDto;
import com.jpcode.screentrack.integration.omdb.dto.OmdbResponseDto;
import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.review.Rating;
import com.jpcode.screentrack.review.Review;
import com.jpcode.screentrack.review.ReviewRepository;
import com.jpcode.screentrack.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private FeedService feedService;

    private User user;
    private Media media;


    @BeforeEach
    void setUp() {

        user = new User();

        OmdbResponseDto dto = new OmdbResponseDto(
                "Interstellar",
                "2014",
                "tt0816692",
                MediaType.MOVIE.name(),
                "poster",
                "Plot",
                "Christopher Nolan",
                "Matthew McConaughey",
                "Adventure",
                "True"
        );

        media = new Media(dto);
    }


    @Test
    void findReviewsShouldReturnPaginatedReviews() {

        Review review = new Review(
                Rating.FIVE,
                "Excellent movie",
                user,
                media
        );

        PageRequest pageable = PageRequest.of(0, 10);

        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(review)));


        Page<FeedReviewResponseDto> response =
                feedService.findReviews(pageable);


        assertThat(response)
                .hasSize(1);

        FeedReviewResponseDto feedReview = response.getContent().get(0);

        assertThat(feedReview.rating())
                .isEqualTo(Rating.FIVE);

        assertThat(feedReview.comment())
                .isEqualTo("Excellent movie");

        assertThat(feedReview.createdAt())
                .isNotNull();

        assertThat(feedReview.media().title())
                .isEqualTo("Interstellar");
    }


    @Test
    void findReviewsWhenNoReviewsShouldReturnEmptyPage() {

        PageRequest pageable = PageRequest.of(0, 10);

        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(Page.empty());


        Page<FeedReviewResponseDto> response =
                feedService.findReviews(pageable);


        assertThat(response)
                .isEmpty();
    }
}