package com.jpcode.screentrack.watchlist;

import com.jpcode.screentrack.watchlist.dto.CreateWatchlistRequestDto;
import com.jpcode.screentrack.watchlist.dto.WatchlistResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    public WatchlistController(WatchlistService watchlistService) {

        this.watchlistService = watchlistService;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<List<WatchlistResponseDto>> findByUsername(@PathVariable String username) {
        return ResponseEntity
                .ok(
                        watchlistService.findByUsername(username)
                );
    }

    @PostMapping
    public ResponseEntity<WatchlistResponseDto> create(@RequestBody @Valid CreateWatchlistRequestDto dto) {
        return ResponseEntity.ok(
                watchlistService.create(dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        watchlistService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<WatchlistResponseDto>> findMyWatchlist() {
        return ResponseEntity.ok(
                watchlistService.findMyWatchList()
        );
    }
}
