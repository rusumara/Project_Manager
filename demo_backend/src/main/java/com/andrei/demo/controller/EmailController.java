package com.andrei.demo.controller;

import com.andrei.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/test-email")
    public String sendTestEmail() {

        emailService.sendEmail(
                "mararusu11@gmail.com",
                "Spring Boot Test",
                "Email sending works!"
        );

        return "Email sent!";
    }
}