package com.example.springdemo.exercise;

import com.example.springdemo.entities.User;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(User user) {
        System.out.println("Sending email to: " + user.getEmail());
    }
}