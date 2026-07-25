package com.jpcode.screentrack.watchlist;

import com.jpcode.screentrack.media.Media;
import com.jpcode.screentrack.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    boolean existsByUserAndMedia(User user, Media media);


    Optional<Watchlist> findByUserAndId(User user, Long id);


    List<Watchlist> findByUserOrderByIdDesc(User user);

    void deleteByUserAndMedia(User user, Media media);

}
