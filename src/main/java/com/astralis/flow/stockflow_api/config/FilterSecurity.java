package com.astralis.flow.stockflow_api.config;

import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties.Http;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.astralis.flow.stockflow_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class FilterSecurity {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // actuator health (Docker healthcheck)
            .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

            // request user
            .requestMatchers(HttpMethod.POST, "/users/create").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/users/getAll").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
            .requestMatchers(HttpMethod.GET, "/users/email/{email}").authenticated()
            .requestMatchers(HttpMethod.PUT, "/users/update/{id}").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/users/{id}/password").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/users/delete/{id}").authenticated()

            // stock request
            .requestMatchers(HttpMethod.GET, "/stock/getLotByLocation/{location}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getLotById/{Id}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getLotesByProductId/{id}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getItemByDescription/{description}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getItemByCod/{cod}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getItemById/{id}").authenticated()
            .requestMatchers(HttpMethod.GET, "/stock/getClientById/{id}").authenticated()

            // order-production request (SUPERVISOR, PRODUCTION, ADMIN)
            .requestMatchers("/order-production/**").hasAnyRole("SUPERVISOR", "PRODUCTION", "ADMIN")

            // order-production-items request (SUPERVISOR, PRODUCTION, ADMIN)
            .requestMatchers("/order-production-items/**").hasAnyRole("SUPERVISOR", "PRODUCTION", "ADMIN")

            // orders/pedidos request (ADMIN, PICKER, SUPERVISOR)
            .requestMatchers("/orders/**").hasAnyRole("ADMIN", "PICKER", "SUPERVISOR")

            .anyRequest().authenticated()

        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
    return authConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
