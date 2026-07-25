package com.jpcode.screentrack.user;

import com.jpcode.screentrack.email.EmailService;
import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.security.AuthenticatedUserService;
import com.jpcode.screentrack.user.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticatedUserService authenticatedUserService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticatedUserService authenticatedUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticatedUserService = authenticatedUserService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCaseAndVerifiedTrueAndActiveTrue(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }

    @Transactional
    public User register(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new BusinessRuleException("There is already an account with that email address.");
        }

        if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
            throw new BusinessRuleException("There is already an account with that username.");
        }

        var encryptedPassword = passwordEncoder.encode(dto.password());

        var user = new User(dto, encryptedPassword);

        userRepository.save(user);

        emailService.sendEmailVerification(user);

        return user;
    }

    @Transactional
    public void verifyEmail(String code) {
        User user = userRepository.findByTokenVerification(code).orElseThrow(() ->
                new BusinessRuleException("Invalid verification token"));
        if (user.isVerified()) {
            throw new BusinessRuleException("This account is already verified.");
        }
        if (user.getExpirationDateToken().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Verification token is expired.");
        }
        user.verify();
        userRepository.save(user);
    }

    public User searchById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessRuleException("User not found"));
    }
    
    @Transactional
    public void resendEmailVerification(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BusinessRuleException("User not found"));
        if (user.isVerified()) {
            throw new BusinessRuleException(
                    "This account is already verified."
            );
        }
        user.genNewVerifiedToken();
        emailService.sendEmailVerification(user);
    }

    public User searchByUsername(String username) {
        return userRepository.findByUsernameIgnoreCaseAndVerifiedTrueAndActiveTrue(username).orElseThrow(
                () -> new BusinessRuleException("User not found")
        );
    }

    @Transactional
    public User editProfile(UserUpdateRequestDto dto) {

        User user = authenticatedUserService.getAuthenticatedUser();
        if (dto.username() != null &&
            userRepository.existsByUsernameIgnoreCase(dto.username())
            && !dto.username().equalsIgnoreCase(user.getUserName())) {
            throw new BusinessRuleException("This username is not available.");
        }
        userRepository.save(user);
        return user.updateData(dto);
    }

    @Transactional
    public void changePassword(UserUpdatePasswordDto dto) {
        User logged = authenticatedUserService.getAuthenticatedUser();
        if(!passwordEncoder.matches(dto.currentPassword(), logged.getPassword())) {
            throw new BusinessRuleException("Passwords do not match.");
        }

        if (!dto.newPassword().equals(dto.newPasswordConfirmation())) {
            throw new BusinessRuleException("Passwords do not match");
        }

        if (passwordEncoder.matches(dto.newPassword(), logged.getPassword())) {
            throw new BusinessRuleException("New password must be different from the current one.");
        }

        String encryptedPassword = passwordEncoder.encode(dto.newPassword());
        logged.updatePassword(encryptedPassword);
        userRepository.save(logged);
    }

    @Transactional
    public void desactivateUser(Long id) {
        var logged = authenticatedUserService.getAuthenticatedUser();
        if (logged.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can desactivate users.");
        }
        var user =  userRepository.findById(id).orElseThrow(() ->
                new BusinessRuleException("User not found"));

        user.desativate();
        userRepository.save(user);
    }

    @Transactional
    public void reactivateUser(Long id) {
        User logged = authenticatedUserService.getAuthenticatedUser();

        if (logged.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessRuleException("User not found")
                );

        user.reactivate();
        userRepository.save(user);
    }

    @Transactional
    public void deactiveMyAccount(UserDeactiveAccountDto dto) {
        User user = authenticatedUserService.getAuthenticatedUser();
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BusinessRuleException("Current passwords do not match.");
        }
        user.desativate();
        userRepository.save(user);
    }
}
