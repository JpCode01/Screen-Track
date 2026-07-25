package com.jpcode.screentrack.favorite;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.user.User;
import jakarta.persistence.*;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "media_id"
                        }
                )
        }
)
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    public MediaType getMediaType() {
        return mediaType;
    }

    @Deprecated
    public Favorite() {
    }
    
    public Favorite(Integer position, Media media, User user) {
        this.position = position;
        this.media = media;
        this.user = user;
        this.mediaType = media.getType();
    }

    public Long getId() {
        return id;
    }

    public Integer getPosition() {
        return position;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public void changePosition(Integer position) {
        this.position = position;
    }
}
