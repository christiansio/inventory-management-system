package com.personal.ims.auth.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.personal.ims.auth.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    @Value("${ims.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16, // Salt length: 16 bytes
                32, // Hash length: 32 bytes
                1, // Parallelism
                19456, // Memory: 19 MiB (KiB)
                2); // Iterations
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        // The authentication provider uses the CustomUserDetailsService to verify the
        // credentials of the user
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

        // set password encoder
        provider.setPasswordEncoder(passwordEncoder());

        // returns configured auth provider so Spring can use it
        return provider;
    }

    // AuthenticationManager is the entry point of login code
    // it delegates the check to the configured provider
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource())) // configures CORS using corsConfiguration method below
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)) // Tells Spring security to not use HTTP session to persist
                                                          // security context
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()) // authorize http request to only permit path with /auth/** unauthenticated
                .authenticationProvider(
                        authenticationProvider()) // sets auth provider
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class); // sets filter before authentication

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // set origins that are allowed to communicate with this app's backend
        config.setAllowedOriginPatterns(allowedOrigins);

        // set allowed HTTP methods the backend can receive and process
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"));

        // Set Request Headers that the backend can receive and process
        config.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "X-Correlation-Id"));

        // Expose named headers to browser js on allowed cross-origin requests
        // config.setExposedHeaders(List.of("X-Correlation-Id"));

        // Use URLs and configure URLs for the CORs rules to be applied on
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply CORs rules to all paths
        source.registerCorsConfiguration(
                "/**",
                config);

        return source;
    }

}
