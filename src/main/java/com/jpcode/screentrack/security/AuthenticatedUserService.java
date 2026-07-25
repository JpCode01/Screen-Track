package com.jpcode.screentrack.security;

import com.jpcode.screentrack.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {
    public User getAuthenticatedUser() {
        Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("There is no user logged in");
        }
        return (User) authentication.getPrincipal();
    }
}
