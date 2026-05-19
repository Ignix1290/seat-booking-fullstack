package com.samuel.booking;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(

            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("Auth Header received: " + request.getHeader("Authorization"));
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("RAW TOKEN RECEIVED BY BACKEND: [" + jwt + "]");

        username = jwtService.extractUsername(jwt);

        if (username != null && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() == null) {
            // This part tells Spring "This user is authenticated"
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            username, null, java.util.Collections.emptyList()
                    );

            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authToken);

            // Now that the bouncer knows who you are, let the request through
            filterChain.doFilter(request, response);
        }
    }
}