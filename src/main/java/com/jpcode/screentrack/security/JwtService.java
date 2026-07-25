package com.jpcode.screentrack.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Screen Track")
                    .withSubject(user.getUsername())
                    .withExpiresAt(expirationAccessToken())
                    .withClaim("type", "access")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new BusinessRuleException("An Error ocurred generating the access Token");
        }
    }

    public String generateRefreshToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Screen Track")
                    .withSubject(user.getId().toString())
                    .withExpiresAt(expirationRefreshToken())
                    .withClaim("type", "refresh")
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new BusinessRuleException("An Error ocurred generating the refresh Token");
        }
        
    }

    public String verifyToken(String token) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Screen Track")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            throw new BusinessRuleException("An Error ocurred verifying the token");
        }
    }


    private Instant expirationAccessToken()
    {
        return LocalDateTime.now()
                .plusMinutes(30)
                .toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant expirationRefreshToken()
    {
        return LocalDateTime.now()
                .plusDays(7)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}


