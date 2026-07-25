package com.jpcode.screentrack.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCaseAndVerifiedTrueAndActiveTrue(String email);
    Optional<User> findByTokenVerification(String codigo);
    Optional<User> findByUsernameIgnoreCaseAndVerifiedTrueAndActiveTrue(String nomeUsuario);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String nomeUsuario);


    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsername(String username);


}
