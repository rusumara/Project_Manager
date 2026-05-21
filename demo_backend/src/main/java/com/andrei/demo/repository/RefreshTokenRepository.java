package com.andrei.demo.repository;

import com.andrei.demo.model.Person;
import com.andrei.demo.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByPerson(Person person);
    void deleteByExpiryDateBefore(LocalDateTime cutoff);
}
