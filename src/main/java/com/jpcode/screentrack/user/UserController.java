package com.jpcode.screentrack.user;

import com.jpcode.screentrack.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRegisterRequestDto dto, UriComponentsBuilder builder) {
        var user = userService.register(dto);
        var uri = builder.path("/users/{username}").buildAndExpand(user.getUserName()).toUri();
        return ResponseEntity.created(uri).body(new UserResponseDto(user));
    }

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String code) {
        userService.verifyEmail(code);
        return ResponseEntity.ok("Account verified.");
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String username) {
        var user = userService.searchByUsername(username);
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserResponseDto> updateUser(
            @RequestBody @Valid UserUpdateRequestDto dto) {

        var user = userService.editProfile(dto);
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @PatchMapping("/users/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid UserUpdatePasswordDto dto) {
        userService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/me")
    public ResponseEntity<Void> deactivateUserOwnAccount(@RequestBody @Valid UserDeactiveAccountDto dto) {
        userService.deactiveMyAccount(dto);
        return ResponseEntity.noContent().build();
    }
}
