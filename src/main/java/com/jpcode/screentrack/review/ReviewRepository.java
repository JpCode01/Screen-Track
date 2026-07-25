package com.jpcode.screentrack.review;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndMedia(User user, Media media);

    List<Review> findByMedia(Media media);

    List<Review> findByUser(User user);

    boolean existsByUserAndMedia(User user, Media media);

    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
