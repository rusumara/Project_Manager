package com.andrei.demo.service;

import com.andrei.demo.model.LoginResponse;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.RefreshToken;
import com.andrei.demo.model.UserRole;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
@AllArgsConstructor
public class SecurityService {

    private final PersonRepository personRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditService auditService;

    @Transactional
    public LoginResponse login(
            String email,
            String password
    ) {

        Optional<Person> optionalPerson =
                personRepository.findByEmail(email);


        if (optionalPerson.isEmpty()) {
            return new LoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Invalid email or password"
            );
        }
        Person person = optionalPerson.get();
        auditService.log(
                "LOGIN",
                person.getEmail());



        if (!passwordEncoder.matches(
                password,
                person.getPassword()
        )) {

            return new LoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Invalid email or password"
            );
        }

        UserRole role =
                person.getRole() != null
                        ? person.getRole()
                        : UserRole.USER;

        String accessToken =
                jwtService.generateToken(
                        person.getEmail(),
                        role
                );
        refreshTokenRepository.deleteByPerson(person);
        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        refreshToken.setPerson(person);

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(
                true,
                accessToken,
                refreshToken.getToken(),
                role.name(),
                null
        );
    }

    @Transactional
    public LoginResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            auditService.log("REFRESH_TOKEN_EXPIRED", refreshToken.getPerson().getEmail());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        Person person = refreshToken.getPerson();
        UserRole role = person.getRole() != null ? person.getRole() : UserRole.USER;
        String newAccessToken = jwtService.generateToken(person.getEmail(), role);

        auditService.log("REFRESH_TOKEN_USED", person.getEmail());
        return new LoginResponse(true, newAccessToken, refreshToken.getToken(), role.name(), null);
    }
}