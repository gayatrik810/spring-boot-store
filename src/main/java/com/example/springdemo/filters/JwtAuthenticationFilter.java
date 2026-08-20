package com.example.springdemo.filters;

import com.example.springdemo.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Login and refresh do NOT need JWT
        String path = request.getServletPath();

        if (path.equals("/auth/login") ||
                path.equals("/auth/refresh")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);

        // No token → continue
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        System.out.println("TOKEN RECEIVED");

        var jwt = jwtService.parseToken(token);

        if (jwt == null || jwt.isExpired()) {

            System.out.println("TOKEN INVALID OR EXPIRED");

            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            System.out.println("USER ID = " + jwt.getUserId());
            System.out.println("ROLE = " + jwt.getRole());

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwt.getUserId(),
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + jwt.getRole()
                                    )
                            )
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}