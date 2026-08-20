package com.example.springdemo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("Test@123");

        System.out.println("Hash: " + hash);
        System.out.println("Length: " + hash.length());
    }
}