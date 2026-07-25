package com.jpcode.screentrack.favorite;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.media.MediaType;
import com.jpcode.screentrack.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndMedia(
            User user,
            Media media
    );


    List<Favorite> findByUserOrderByPositionAsc(
            User user
    );


    long countByUserAndMediaType(
            User user,
            MediaType type
    );


    boolean existsByUserAndPositionAndMediaType(
            User user,
            Integer position,
            MediaType mediaType
    );

    boolean existsByUserAndPositionAndMediaTypeAndIdNot(
            User user,
            Integer position,
            MediaType mediaType,
            Long id
    );


    boolean existsByUserAndPositionAndIdNot(
            User user,
            Integer position,
            Long id
    );


    Optional<Favorite> findByUserAndId(
            User user,
            Long id
    );
    List<Favorite> findByUserAndMediaTypeOrderByPositionAsc(
            User user,
            MediaType mediaType
    );


    boolean existsByUserAndMedia(User user, Media media);
}
    