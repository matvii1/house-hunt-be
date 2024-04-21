package com.house.hunter.config;

import com.house.hunter.service.CustomUserDetailsService;
import com.house.hunter.util.PasswordEncoder;
import com.house.hunter.util.SecretKeyGenerator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@AllArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(PasswordEncoder.getPasswordEncoder());
        return authenticationManagerBuilder.build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
              /*          .requestMatchers("/token/**").permitAll()
                        .requestMatchers("/api/v1/user/**").hasAnyRole(UserRole.ADMIN.getRole(), UserRole.LANDLORD.getRole(), UserRole.TENANT.getRole())
                        .requestMatchers("/api/v1/auth/**").hasRole(UserRole.LANDLORD.name())
                        .requestMatchers("/api/v1/tenant/**").hasRole(UserRole.TENANT.name())*/
                        .anyRequest().permitAll()
                )

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            /*    .requestMatchers("/api/admin/**").hasRole(UserRole.ADMIN.name())
                .requestMatchers("/api/landlord/**").hasRole(UserRole.LANDLORD.name())
                .requestMatchers("/api/tenant/**").hasRole(UserRole.TENANT.name())*/
                .build();

    }

    @Bean(name = "secretKeyGenerator")
    public SecretKeyGenerator secretKeyGenerator() {
        return new SecretKeyGenerator();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return PasswordEncoder.getPasswordEncoder();
    }
}

