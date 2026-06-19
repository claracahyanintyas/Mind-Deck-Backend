package org.individualproject.flashcards.infrastructure.security;

import org.individualproject.flashcards.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins; // comma-separated

    public SpringSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register it globally for ALL endpoints including root/auth paths
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationEntryPoint authenticationEntryPoint, // FIX: Injected directly here
            JwtAuthenticationFilter authenticationFilter          // FIX: Injected directly here
    ) throws Exception {

        String deckPath = "/api/decks/**";
        String cardPath = "/api/cards/**";
        String authPath = "/api/auth/**";
        String userPath = "/api/users/**";
        String reviewPath = "/api/reviews/**";
        String classroomPath = "api/classroom/**";

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET, deckPath).permitAll();
                    authorize.requestMatchers(HttpMethod.POST, deckPath).permitAll();
                    authorize.requestMatchers(HttpMethod.DELETE, deckPath).permitAll();
                    authorize.requestMatchers(HttpMethod.PUT, deckPath).permitAll();
                    authorize.requestMatchers(HttpMethod.GET, cardPath).permitAll();
                    authorize.requestMatchers(HttpMethod.POST, cardPath).permitAll();
                    authorize.requestMatchers(HttpMethod.DELETE, cardPath).permitAll();
                    authorize.requestMatchers(HttpMethod.POST, authPath).permitAll();
                    authorize.requestMatchers(HttpMethod.GET, userPath).permitAll();
                    authorize.requestMatchers(HttpMethod.GET, reviewPath).authenticated();
                    authorize.requestMatchers(HttpMethod.POST, classroomPath).authenticated();
                    authorize.anyRequest().authenticated();
                })
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint)
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .securityContext(securityContext ->
                        securityContext.requireExplicitSave(false)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}