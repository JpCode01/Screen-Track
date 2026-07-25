package com.jpcode.screentrack.user;

import com.jpcode.screentrack.email.EmailService;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.dto.UserDeactiveAccountDto;
import com.jpcode.screentrack.user.dto.UserRegisterRequestDto;
import com.jpcode.screentrack.user.dto.UserUpdatePasswordDto;
import com.jpcode.screentrack.user.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void registerWhenValidRequestShouldCreateUser() {
        // Arrange
        UserRegisterRequestDto dto = new UserRegisterRequestDto(
                        "user@email.com",
                        "123456",
                        "John",
                        "john",
                        "bio"
                );
        when(userRepository.existsByEmailIgnoreCase(dto.email()))
                .thenReturn(false);

        when(userRepository.existsByUsernameIgnoreCase(dto.username()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.password()))
                .thenReturn("encrypted-password");


        // Act

        User result = userService.register(dto);


        // Assert
        assertThat(result)
                .isNotNull();


        assertThat(result.getEmail())
                .isEqualTo("user@email.com");


        verify(userRepository)
                .save(any(User.class));


        verify(emailService)
                .sendEmailVerification(any(User.class));
    }

    @Test
    void registerWhenEmailAlreadyExistsShouldThrowException() {

        // Arrange
        UserRegisterRequestDto dto =
                new UserRegisterRequestDto(
                        "user@email.com",
                        "123456",
                        "John",
                        "john",
                        null
                );


        when(userRepository.existsByEmailIgnoreCase(dto.email()))
                .thenReturn(true);


        // Act and   Assert
        assertThatThrownBy(() ->
                userService.register(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "There is already an account with that email address."
                );


        verify(userRepository, never())
                .save(any());

    }

    @Test
    void registerWhenUsernameAlreadyExistsShouldThrowException() {
        // Arrange

        UserRegisterRequestDto dto =
                new UserRegisterRequestDto(
                        "user@email.com",
                        "123456",
                        "John",
                        "john",
                        null
                );


        when(userRepository.existsByEmailIgnoreCase(dto.email()))
                .thenReturn(false);


        when(userRepository.existsByUsernameIgnoreCase(dto.username()))
                .thenReturn(true);

        // Act and Assert

        assertThatThrownBy(() ->
                userService.register(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage(
                        "There is already an account with that username."
                );


        verify(userRepository, never())
                .save(any());

    }

    @Test
    void loadUserByUsernameWhenUserExistsShouldReturnUser() {

        // Arrange
        when(userRepository
                .findByEmailIgnoreCaseAndVerifiedTrueAndActiveTrue(
                        "user@email.com"
                ))
                .thenReturn(Optional.of(user));

        // Act

        var result =
                userService.loadUserByUsername("user@email.com");


        // Assert
        assertThat(result)
                .isEqualTo(user);

    }

    @Test
    void loadUserByUsernameWhenUserDoesNotExistShouldThrowBusinessRuleException() {

        // Arrange
        when(userRepository
                .findByEmailIgnoreCaseAndVerifiedTrueAndActiveTrue(
                        "user@email.com"
                ))
                .thenReturn(Optional.empty());

        // Act and Assert

        assertThatThrownBy(() ->
                userService.loadUserByUsername("user@email.com")
        )
                .isInstanceOf(
                        UsernameNotFoundException.class
                )
                .hasMessage("User not found");

    }

    @Test
    void verifyEmailWhenTokenIsValidShouldActivateUser() {

        // Arrange

        when(userRepository.findByTokenVerification("valid-token"))
                .thenReturn(Optional.of(user));

        // Act

        userService.verifyEmail("valid-token");

        // Assert

        assertThat(user.isVerified())
                .isTrue();

        assertThat(user.isActive())
                .isTrue();

        verify(userRepository)
                .save(user);
    }

    @Test
    void verifyEmailWhenTokenDoesNotExistShouldThrowBusinessRuleException() {

        // Arrange

        when(userRepository.findByTokenVerification("invalid-token"))
                .thenReturn(Optional.empty());

        // Act and Assert

        assertThatThrownBy(() ->
                userService.verifyEmail("invalid-token")
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid verification token");


        verify(userRepository, never())
                .save(any());
    }

    @Test
    void verifyEmailWhenAccountAlreadyVerifiedShouldThrowBusinessRuleException() {
        // Arrange

        user.verify();


        when(userRepository.findByTokenVerification(user.getTokenVerification()))
                .thenReturn(Optional.of(user));
        
        // Act and Assert

        assertThatThrownBy(() ->
                userService.verifyEmail(user.getTokenVerification())
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("This account is already verified.");

    }

    @Test
    void resendEmailVerificationWhenUserExistsShouldSendEmail() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase("user@email.com"))
                .thenReturn(Optional.of(user));
        // Act

        userService.resendEmailVerification("user@email.com");

        // Assert
        verify(emailService)
                .sendEmailVerification(user);

    }

    @Test
    void resendEmailVerificationWhenUserDoesNotExistShouldThrowBusinessRuleException() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase("user@email.com"))
                .thenReturn(Optional.empty());
        
        // Act and Assert

        assertThatThrownBy(() ->
                userService.resendEmailVerification("user@email.com")
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("User not found");


        verify(emailService, never())
                .sendEmailVerification(any());
    }

    @Test
    void resendEmailVerificationWhenAccountAlreadyVerifiedShouldThrowBusinessRuleException() {

        // Arrange
        user.verify();


        when(userRepository.findByEmailIgnoreCase("user@email.com"))
                .thenReturn(Optional.of(user));

        // Act and Asser

        assertThatThrownBy(() ->
                userService.resendEmailVerification("user@email.com")
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("This account is already verified.");

    }

    @Test
    void searchByUsernameWhenUserExistsShouldReturnUser() {
        // Arrange
        when(userRepository
                .findByUsernameIgnoreCaseAndVerifiedTrueAndActiveTrue("john"))
                .thenReturn(Optional.of(user));

        // Act
        User result =
                userService.searchByUsername("john");

        //  Assert
        assertThat(result)
                .isEqualTo(user);

    }

    @Test
    void searchByUsernameWhenUserDoesNotExistShouldThrowBusinessRuleException() {

        // Arrange

        when(userRepository
                .findByUsernameIgnoreCaseAndVerifiedTrueAndActiveTrue("john"))
                .thenReturn(Optional.empty());

        // Act and Assert

        assertThatThrownBy(() ->
                userService.searchByUsername("john")
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("User not found");

    }

    @Test
    void editProfileWhenDataIsValidShouldUpdateUser() {

        // Arrange
        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserUpdateRequestDto dto =
                new UserUpdateRequestDto(
                        "newUsername",
                        "new bio"
                );


        when(userRepository.existsByUsernameIgnoreCase("newUsername"))
                .thenReturn(false);


        // Act
        User result =
                userService.editProfile(dto);

        // Assert

        assertThat(result.getUserName())
                .isEqualTo("newUsername");


        assertThat(result.getBio())
                .isEqualTo("new bio");


        verify(userRepository)
                .save(user);

    }

    @Test
    void editProfileWhenUsernameAlreadyExistsShouldThrowBusinessRuleException() {

        // Arrange
        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserUpdateRequestDto dto =
                new UserUpdateRequestDto(
                        "anotherUser",
                        null
                );


        when(userRepository.existsByUsernameIgnoreCase("anotherUser"))
                .thenReturn(true);


        // Act and Assert
        assertThatThrownBy(() ->
                userService.editProfile(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("This username is not available.");

    }

    @Test
    void changePasswordWhenPasswordIsCorrectShouldUpdatePassword() {

        // Arrange
        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserUpdatePasswordDto dto =
                new UserUpdatePasswordDto(
                        "123456",
                        "newPassword",
                        "newPassword"
                );


        when(passwordEncoder.matches(
                "123456",
                user.getPassword()))
                .thenReturn(true);



        when(passwordEncoder.matches(
                "newPassword",
                user.getPassword()))
                .thenReturn(false);



        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encrypted-new-password");


        // Act
        userService.changePassword(dto);

        // Assert

        verify(passwordEncoder)
                .encode("newPassword");


        verify(userRepository)
                .save(user);

    }
    @Test
    void changePasswordWhenCurrentPasswordIsWrongShouldThrowBusinessRuleException() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserUpdatePasswordDto dto =
                new UserUpdatePasswordDto(
                        "wrong",
                        "newPassword",
                        "newPassword"
                );


        when(passwordEncoder.matches(
                "wrong",
                user.getPassword()))
                .thenReturn(false);



        assertThatThrownBy(() ->
                userService.changePassword(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Passwords do not match.");

    }

    @Test
    void deactivateMyAccountWhenPasswordIsCorrectShouldDeactivateUser() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserDeactiveAccountDto dto =
                new UserDeactiveAccountDto("123456");


        when(passwordEncoder.matches(
                "123456",
                user.getPassword()))
                .thenReturn(true);



        userService.deactiveMyAccount(dto);



        assertThat(user.isActive())
                .isFalse();


        verify(userRepository)
                .save(user);

    }

    @Test
    void deactivateMyAccountWhenPasswordIsWrongShouldThrowBusinessRuleException() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        UserDeactiveAccountDto dto =
                new UserDeactiveAccountDto("wrong");


        when(passwordEncoder.matches(
                "wrong",
                user.getPassword()))
                .thenReturn(false);



        assertThatThrownBy(() ->
                userService.deactiveMyAccount(dto)
        )
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Current passwords do not match.");

    }

    @Test
    void deactivateUserWhenLoggedUserIsAdminShouldDeactivateUser() {

        User admin = new User(
                new UserRegisterRequestDto(
                        "admin@email.com",
                        "123456",
                        "Admin",
                        "admin",
                        null
                ),
                "encrypted-password"
        );

        // Precisamos transformar em ADMIN
        ReflectionTestUtils.setField(
                admin,
                "role",
                Role.ADMIN
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(admin);


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        userService.desactivateUser(1L);


        assertThat(user.isActive())
                .isFalse();


        verify(userRepository)
                .save(user);
    }

    @Test
    void deactivateUserWhenLoggedUserIsNotAdminShouldThrowException() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        assertThatThrownBy(() ->
                userService.desactivateUser(1L)
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(
                        "Only admins can desactivate users."
                );


        verify(userRepository, never())
                .findById(any());

    }

    @Test
    void reactivateUserWhenLoggedUserIsAdminShouldReactivateUser() {


        User admin = new User(
                new UserRegisterRequestDto(
                        "admin@email.com",
                        "123456",
                        "Admin",
                        "admin",
                        null
                ),
                "encrypted-password"
        );


        ReflectionTestUtils.setField(
                admin,
                "role",
                Role.ADMIN
        );


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(admin);


        user.desativate();


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));



        userService.reactivateUser(1L);



        assertThat(user.isActive())
                .isTrue();


        verify(userRepository)
                .save(user);

    }

    @Test
    void reactivateUserWhenLoggedUserIsNotAdminShouldThrowException() {


        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(user);


        assertThatThrownBy(() ->
                userService.reactivateUser(1L)
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(
                        "Access denied."
                );


        verify(userRepository, never())
                .save(any());

    }




}