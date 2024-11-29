package org.example.profilecase5.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = "/";

        // Kiểm tra nếu là login qua OAuth2
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            // Kiểm tra các quyền của người dùng OAuth2
            // OAuth2User là người dùng đã được xác thực qua OAuth2
            Collection<? extends GrantedAuthority> authorities = oauth2Token.getAuthorities();

            // Kiểm tra quyền của người dùng
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                targetUrl = "/admin";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                targetUrl = "/home";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
                targetUrl = "/hosting";  // Điều hướng đến hosting nếu ROLE_OWNER
            }
        } else {
            // Xử lý login thông thường (username/password)
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                targetUrl = "/admin";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                targetUrl = "/home";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
                targetUrl = "/hosting";
            }
        }

        // Kiểm tra REDIRECT_URL và điều hướng lại nếu có
        String redirectUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            targetUrl = redirectUrl;
        }

        // Xóa REDIRECT_URL trong session
        request.getSession().removeAttribute("REDIRECT_URL");

        // Chuyển hướng người dùng tới trang mục tiêu
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}