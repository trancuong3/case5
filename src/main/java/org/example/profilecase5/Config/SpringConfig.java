package org.example.profilecase5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SpringConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(registry->{
                    registry.requestMatchers("/","/login","/css/**","/images/**").permitAll();
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(oauth2Login -> {
                    oauth2Login
                            .loginPage("/login")
                            .successHandler((request, response, authentication) -> response.sendRedirect("/userProfile"));
                })
//                .formLogin(Customizer.withDefaults())
                .build();
    }
}
