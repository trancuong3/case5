package org.example.profilecase5.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.profilecase5.Service.CustomOAuth2UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.CustomerUserDetailService;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.common.CustomAuthenticationEntryPoint;
import org.example.profilecase5.common.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomerUserDetailService customerUserDetailService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private UserService userService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customerUserDetailService)
                .authorizeRequests(auth -> auth
                        // Các URL không yêu cầu xác thực
                        .requestMatchers("/home/detail/**", "/main", "/register", "/registerOwner", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Chỉ OWNER, ADMIN, USER được truy cập các URL tương ứng
                        .requestMatchers("/hosting").hasRole("OWNER")
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/home").hasRole("USER")
                        // Các yêu cầu khác yêu cầu xác thực
                        .anyRequest().authenticated()
                )
                // Cấu hình đăng nhập form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // Cấu hình OAuth2 Login
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")  // Cấu hình trang login tùy chỉnh (nếu cần)
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig
                                        .userService(customOAuth2UserService)  // Đăng ký CustomOAuth2UserService
                        ) // Đăng ký CustomOAuth2UserService// Đăng ký CustomOAuth2UserService
                        .successHandler(customAuthenticationSuccessHandler)  // Xử lý khi đăng nhập thành công
                        .failureUrl("/login?error=true")  // Địa chỉ khi đăng nhập thất bại
                        .permitAll() // Redirect sau khi login thành công
                )
                // Xử lý ngoại lệ
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // Cấu hình CSRF
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/profile/update-avatar")
                )
                // Cấu hình đăng xuất
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")  // URL đăng xuất
                        .logoutSuccessUrl("/main")     // Chuyển hướng đến /main sau khi đăng xuất
                        .deleteCookies("JSESSIONID")   // Xóa cookie
                        .invalidateHttpSession(true)   // Hủy session
                        .clearAuthentication(true)     // Xóa thông tin xác thực
                        .permitAll()
                )
                // Cấu hình quản lý session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );
        return http.build();
    }

}
