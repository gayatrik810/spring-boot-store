package com.example.springdemo.services;

import com.example.springdemo.entities.User;
import com.example.springdemo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        return userRepository
                .findById(userId)
                .orElseThrow();
    }
}