package com.jpcode.screentrack.review;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rating rating;
    @Column(length = 2000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Deprecated
    public Review() {
    }

    public Review(Rating rating, String comment, User user, Media media) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
        this.media = media;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Rating getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    void edit(Rating rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
