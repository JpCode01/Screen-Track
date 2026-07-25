package com.jpcode.screentrack.auth;

import com.jpcode.screentrack.auth.dto.LoginRequestDto;
import com.jpcode.screentrack.security.JwtService;
import com.jpcode.screentrack.security.dto.JwtDto;
import com.jpcode.screentrack.security.dto.JwtRefreshDto;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginRequestDto dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user); return ResponseEntity.ok(new JwtDto(accessToken, refreshToken));
        
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtDto> refreshToken(@Valid @RequestBody JwtRefreshDto dto) {
        var refreshToken = dto.refreshToken();
        Long idUser = Long.valueOf(jwtService.verifyToken(refreshToken));
        var user = userRepository.findById(idUser).orElseThrow();
        String accessToken = jwtService.generateToken(user);
        String refreshedToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(new JwtDto(accessToken, refreshedToken));
    }
}
