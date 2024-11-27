package org.example.profilecase5.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomerUserDetailService customerUserDetailService;

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
                        .loginPage("/login") // Trang login mặc định
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect("/register")) // Redirect sau khi login thành công
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
                )
                .oauth2Login(oauth2Login -> {
                    oauth2Login
                            .loginPage("/login") // Trang login mặc định
                            .successHandler((request, response, authentication) -> {
                                // Lấy token từ Authentication
                                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
                                OAuth2User user = auth.getPrincipal();
                                String email = user.getAttribute("email");
                                String fullName = user.getAttribute("name");
                                String avatarUrl = user.getAttribute("avatar_url");

                                if(!userService.isEmailExist(email)){
                                    User userGG = new User();
                                    userGG.setEmail(email);
                                    userGG.setFullname(fullName);
                                    String randomUsername = generateRandomUsername();  // Hàm tạo username ngẫu nhiên
                                    userGG.setUsername(randomUsername);
                                    userGG.setAvatar(avatarUrl);
                                    String randomPassword = generateRandomPassword();
                                    userGG.setPassword(randomPassword);
                                    userGG.setConfirmPassword(randomPassword);
                                    String randomPhone = generateRandomPhone();
                                    userGG.setPhone(randomPhone);
                                    userService.registerUser(userGG);
                                }
                                User userGG = userService.getUserByEmail(email);
                                String roleUserGG = switch (userGG.getRole().getRoleName()) {
                                    case "ROLE_USER" -> "USER";
                                    case "ROLE_OWNER" -> "OWNER";
                                    case "ROLE_ADMIN" -> "ADMIN";
                                    default -> "USER";
                                };
                                //authenticateUser(userGG.getUsername(), userGG.getPassword(), roleUserGG, request, response);
                            });
                });
        return http.build();
    }

    private void authenticateUser(String username, String password, String selectedRole, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            boolean isValid = userService.validateUserAndRole(username, password, selectedRole);
            if (isValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, password);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authToken);
                User user = userService.getUserByUsername(username);
                HttpSession session = request.getSession();

                session.setAttribute("userId", user.getUserId());

                if ("user".equals(selectedRole)) {
                    response.sendRedirect("/home");
                } else if ("owner".equals(selectedRole)) {
                    response.sendRedirect("/hosting");
                } else if ("admin".equals(selectedRole)) {
                    response.sendRedirect("/admin");
                } else {
                    response.sendRedirect("/login");
                }
            } else {
                response.sendRedirect("/login?error=Invalid credentials or role");
            }
        } catch (Exception e) {
            response.sendRedirect("/login?error=An error occurred during login");
        }
    }


    private String generateRandomPhone() {
        String randomPhone = UUID.randomUUID().toString();
        if (randomPhone.length() > 10) {
            randomPhone = randomPhone.substring(0, 10);
        }
        return randomPhone;
    }

    private String generateRandomPassword() {
        String randomPassword = UUID.randomUUID().toString();

        // Đảm bảo độ dài mật khẩu từ 6 đến 32 ký tự
        if (randomPassword.length() < 6) {
            // Nếu quá ngắn, thêm ký tự cho đủ 6 ký tự
            randomPassword = randomPassword + "123456".substring(0, 6 - randomPassword.length());
        } else if (randomPassword.length() > 32) {
            // Nếu quá dài, cắt chuỗi đến 32 ký tự
            randomPassword = randomPassword.substring(0, 32);
        }

        return randomPassword;
    }
    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString();
    }
}
