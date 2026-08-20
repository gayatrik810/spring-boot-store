package com.example.springdemo.exercise;

import com.example.springdemo.entities.User;
import com.example.springdemo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService1 {

    private final UserRepository repository;
    private final EmailService emailService;

    public UserService1(UserRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public void register(User user) {
        System.out.println("Validating user...");

        repository.save(user);

        emailService.sendEmail(user);

        System.out.println("User registered successfully!");
    }
}