package com.jpcode.screentrack.favorite;

import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.favorite.dto.CreateFavoriteRequestDto;
import com.jpcode.screentrack.favorite.dto.FavoriteResponseDto;
import com.jpcode.screentrack.favorite.dto.UpdateFavoriteRequestDto;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;


    public FavoriteController(FavoriteService favoriteService, UserRepository userRepository, AuthenticatedUserService authenticatedUserService) {
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping
    public ResponseEntity<FavoriteResponseDto> create(@RequestBody @Valid CreateFavoriteRequestDto dto) {
        return ResponseEntity
                .status(201)
                .body(
                        favoriteService.createFavorite(dto)
                );
    }
    
    @PutMapping("/{favoriteId}")
    public ResponseEntity<FavoriteResponseDto> update(@PathVariable Long favoriteId, @RequestBody @Valid UpdateFavoriteRequestDto dto) {
        return ResponseEntity.ok(
                favoriteService.updateFavorite(favoriteId, dto)
        );
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> delete(@PathVariable Long favoriteId) {
        favoriteService.delete(favoriteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/movies")
    public ResponseEntity<List<FavoriteResponseDto>> getFavoriteMovies(@PathVariable String username) {
        return ResponseEntity.ok(
                favoriteService.findMovieByUser(findUser(username))
        );
    }

    @GetMapping("/{username}/series")
    public ResponseEntity<List<FavoriteResponseDto>> getFavoriteSeries(@PathVariable String username) {
        return ResponseEntity.ok(
                favoriteService.findSeriesByUser(findUser(username))
        );

        
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException("User not found"));
    }

    @GetMapping("/me/movies")
    public ResponseEntity<List<FavoriteResponseDto>> getMyFavoriteMovies() {
        return ResponseEntity.ok(
                favoriteService.findMyMovieFavorites()
        );
    }

    @GetMapping("/me/series")
    public ResponseEntity<List<FavoriteResponseDto>> getMyFavoriteSeries() {
        return ResponseEntity.ok(
                favoriteService.findMySeriesFavorites()
        );
    }



}
