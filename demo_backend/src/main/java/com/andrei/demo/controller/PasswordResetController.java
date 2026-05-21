package com.andrei.demo.controller;

import com.andrei.demo.model.ForgotPasswordRequest;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;
import com.andrei.demo.model.ResetPasswordRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password")
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {

    private final PersonRepository personRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

   // @PostMapping("/forgot")
   @PostMapping("/forgot")
   public String forgotPassword(
           @RequestBody ForgotPasswordRequest request
   ) {

       Person person = personRepository
               .findByEmail(request.getEmail())
               .orElseThrow();

       String code = String.valueOf(
               100000 + new Random().nextInt(900000)
       );

       person.setResetCode(code);

       person.setResetCodeExpiration(
               LocalDateTime.now().plusMinutes(10)
       );

       personRepository.save(person);

       emailService.sendResetCode(
               person.getEmail(),
               code
       );

       return "Reset code sent!";
   }

    @PostMapping("/reset")
    public String resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        Person person = personRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        if (!person.getResetCode().equals(request.getCode())) {
            return "Invalid code!";
        }

        if (person.getResetCodeExpiration()
                .isBefore(LocalDateTime.now())) {

            return "Code expired!";
        }

        person.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );
        person.setResetCode(null);
        person.setResetCodeExpiration(null);

        personRepository.save(person);

        emailService.sendPasswordChangedEmail(
                person.getEmail()
        );

        return "Password reset successful!";
    }
}