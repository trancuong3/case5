package org.example.profilecase5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {  // Đổi tên thành SecurityConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(registry -> {
                    // Các URL không yêu cầu xác thực
                    registry.requestMatchers("/", "/login", "/css/**", "/images/**").permitAll();
                    // Các yêu cầu khác yêu cầu người dùng phải xác thực
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(oauth2Login -> {
                    // Cấu hình cho OAuth2 Login
                    oauth2Login
                            .loginPage("/login") // Trang login mặc định
                            .successHandler((request, response, authentication) ->
                                    response.sendRedirect("/register")); // Redirect sau khi login thành công
                })
                .build();
    }
}
