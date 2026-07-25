package com.jpcode.screentrack.watchlist;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.user.User;
import jakarta.persistence.*;

@Entity
@Table(
        name = "watchlists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_media_watchlist",
                        columnNames = {"user_id", "media_id"}
                )
        }
)
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Deprecated
    public Watchlist() {}

    public Watchlist(User user, Media media) {
        this.user = user;
        this.media = media;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }
}
