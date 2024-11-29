package org.example.profilecase5.Service;

import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin người dùng từ OAuth2UserRequest
        OAuth2User oAuth2User = extractOAuth2User(userRequest); // Cần thực hiện thủ công việc lấy thông tin từ userRequest

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        // Kiểm tra người dùng trong DB
        Optional<User> optionalUser = userRepository.findByUsername(email);
        User user;

        // Nếu người dùng chưa có trong DB, tạo mới
        if (optionalUser.isEmpty()) {
            user = new User();
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(generateRandomPassword()));
            user.setPhone(generateRandomPhone());
            user.setEmail(email);
            user.setFullname(fullName);

            // Lấy vai trò người dùng mặc định (ROLE_USER)
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
            user.setRole(userRole); // Gán vai trò cho người dùng

            userRepository.save(user);
        } else {
            // Nếu người dùng đã tồn tại trong DB, lấy thông tin
            user = optionalUser.get();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // Trả về đối tượng OAuth2User với các thông tin và quyền
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())), // Quyền của người dùng
                oAuth2User.getAttributes(), // Các thuộc tính của người dùng từ OAuth2
                "email" // Chỉ định key cho OAuth2User
        );
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
        return "user_" + UUID.randomUUID();
    }

    private String generateRandomPhone() {
        String randomPhone = UUID.randomUUID().toString();
        if (randomPhone.length() > 10) {
            randomPhone = randomPhone.substring(0, 10);
        }
        return randomPhone;
    }

    private String getFullnameFromOAuth2User() {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Lấy fullname từ thông tin của OAuth2 user (Google)
        String fullname = (String) attributes.get("name"); // Google trả về fullname ở trường "name"

        return fullname;
    }

    private OAuth2User extractOAuth2User(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        return delegate.loadUser(userRequest);
    }


}
