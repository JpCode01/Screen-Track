package com.jpcode.screentrack.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.user.User;
import com.jpcode.screentrack.user.dto.UserRegisterRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {


    private JwtService jwtService;

    private User user;


    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "test-secret-key"
        );


        user = new User(
                new UserRegisterRequestDto(
                        "user@email.com",
                        "123456",
                        "John",
                        "john",
                        "bio"
                ),
                "encrypted-password"
        );


        ReflectionTestUtils.setField(
                user,
                "id",
                1L
        );

    }
    @Test
    void generateTokenWhenUserIsValidShouldReturnToken() {


        String token = jwtService.generateToken(user);


        assertThat(token)
                .isNotNull()
                .isNotBlank();


        DecodedJWT decoded =
                JWT.decode(token);


        assertThat(decoded.getIssuer())
                .isEqualTo("Screen Track");


        assertThat(decoded.getSubject())
                .isEqualTo(user.getUsername());


        assertThat(decoded.getClaim("type").asString())
                .isEqualTo("access");

    }

    @Test
    void generateRefreshTokenWhenUserIsValidShouldReturnToken() {


        String token =
                jwtService.generateRefreshToken(user);


        assertThat(token)
                .isNotNull()
                .isNotBlank();


        DecodedJWT decoded =
                JWT.decode(token);


        assertThat(decoded.getIssuer())
                .isEqualTo("Screen Track");


        assertThat(decoded.getSubject())
                .isEqualTo(user.getId().toString());


        assertThat(decoded.getClaim("type").asString())
                .isEqualTo("refresh");

    }

    @Test
    void verifyTokenWhenTokenIsValidShouldReturnUserId() {


        String token =
                jwtService.generateRefreshToken(user);



        String result =
                jwtService.verifyToken(token);



        assertThat(result)
                .isEqualTo(user.getId().toString());

    }

    @Test
    void verifyTokenWhenTokenIsInvalidShouldThrowException() {


        assertThatThrownBy(() ->
                jwtService.verifyToken("invalid-token")
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "An Error ocurred verifying the token"
                );

    }

    @Test
    void verifyTokenWhenTokenWasModifiedShouldThrowException() {


        String token =
                jwtService.generateRefreshToken(user);


        String modifiedToken =
                token.substring(0, token.length() - 5)
                        + "abcde";



        assertThatThrownBy(() ->
                jwtService.verifyToken(modifiedToken)
        )
                .isInstanceOf(BusinessRuleException.class);

    }

}

