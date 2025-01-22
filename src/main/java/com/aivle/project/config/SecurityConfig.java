package com.aivle.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login","/signup","/api/generateEmployeeId","/password-find").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/css/**","/assets/**","/data/**","/js/**").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth)->auth.loginPage("/login")
                        .loginProcessingUrl("/user-login")
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 /으로 리다이렉트
                        .failureUrl("/login?error=true") // 로그인 실패 시 쿼리 파라미터로 error추가
                        .permitAll()
                );
        http
                .csrf((auth)-> auth.disable());

        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true));
        http
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/"));
        http
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedPage("/") // 권한 부족 시 리다이렉트할 페이지
                );

        return http.build();
    }
}
