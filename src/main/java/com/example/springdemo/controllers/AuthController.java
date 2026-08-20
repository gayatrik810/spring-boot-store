package com.example.springdemo.controllers;

import com.example.springdemo.config.JwtConfig;
import com.example.springdemo.dtos.JwtResponse;
import com.example.springdemo.dtos.LoginRequest;
import com.example.springdemo.dtos.UserDto;
import com.example.springdemo.entities.User;
import com.example.springdemo.mappers.UserMapper;
import com.example.springdemo.repositories.UserRepository;
import com.example.springdemo.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        System.out.println("========== LOGIN START ==========");
        System.out.println("Email: " + request.getEmail());

        // 1. Authenticate email + password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        System.out.println("AUTHENTICATION SUCCESS");


        // 2. Get user from database
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("User not found")
                );

        System.out.println("USER FOUND: " + user.getEmail());


        // 3. Generate access token
        var accessToken =
                jwtService.generateAccessToken(user);

        System.out.println("ACCESS TOKEN GENERATED");


        // 4. Generate refresh token
        var refreshToken =
                jwtService.generateRefreshToken(user);

        System.out.println("REFRESH TOKEN GENERATED");


        // 5. Store refresh token in cookie
        Cookie cookie =
                new Cookie(
                        "refreshToken",
                        refreshToken.toString()
                );

        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(
                jwtConfig.getRefreshTokenExpiration()
        );
        cookie.setSecure(false);

        response.addCookie(cookie);


        System.out.println("LOGIN SUCCESS");
        System.out.println("==============================");


        // 6. Return access token
        return ResponseEntity.ok(
                new JwtResponse(
                        accessToken.toString()
                )
        );
    }


    // =========================
    // REFRESH TOKEN
    // =========================

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken")
            String refreshToken) {

        var jwt = jwtService.parseToken(refreshToken);

        if (jwt == null || jwt.isExpired()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        var user = userRepository
                .findById(jwt.getUserId())
                .orElseThrow();

        var accessToken =
                jwtService.generateAccessToken(user);

        return ResponseEntity.ok(
                new JwtResponse(
                        accessToken.toString()
                )
        );
    }


    // =========================
    // CURRENT USER
    // =========================

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getPrincipal() == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Long userId =
                (Long) authentication.getPrincipal();

        var user =
                userRepository
                        .findById(userId)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                userMapper.toDto(user)
        );
    }


    // =========================
    // BAD CREDENTIALS
    // =========================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(
            BadCredentialsException ex) {

        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());

    }
}