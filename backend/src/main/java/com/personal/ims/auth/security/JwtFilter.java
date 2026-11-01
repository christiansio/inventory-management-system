package com.personal.ims.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.personal.ims.auth.service.AuthSessionService;
import com.personal.ims.auth.service.CustomUserDetailsService;
import com.personal.ims.auth.service.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthSessionService authSessionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Check if header has correct pre-fix
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        Long userId;

        try {
            claims = jwtService.extractClaims(header.substring(7));
            userId = jwtService.getUserId(claims);
        } catch (Exception exception) {
            filterChain.doFilter(request, response);
            return;
        }

        // if current token is authenticated, validation passes and proceeds to next
        // processes
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails;

            try {
                userDetails = customUserDetailsService.loadByUserId(userId);
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }

            try {

                // token validity, user status, and session validity checks
                if (jwtService.isTokenValid(claims, userId)
                        && userDetails.isEnabled()
                        && authSessionService.isSessionValid(
                                jwtService.getSessionId(claims), userId)) {

                    // Creates a Spring Authentication object that will be used to supply the value
                    // for Security Context
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // capture remote IP address and current HTTP session ID if exists, and store in
                    // authenticationToken, which makes it available through
                    // authentication.getDetails() later
                    authenticationToken
                            .setDetails(new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    // Stores authenticated user's authentication object in SecurityContext
                    // This can be accessed through
                    // SecurityContextHolder.getContext().getAuthentication
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);

    }

}
