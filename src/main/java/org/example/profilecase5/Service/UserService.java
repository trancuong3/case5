package org.example.profilecase5.Service;

import org.example.profilecase5.Exceptions.User.PasswordValidationException;
import org.example.profilecase5.Repository.RoleRepository;
import org.springframework.security.core.Authentication;
import org.example.profilecase5.Exceptions.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exceptions.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.*;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void registerUser(User user) {
        // Kiểm tra tên người dùng
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            // Ném exception nếu mật khẩu không khớp
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra mật khẩu thô có thỏa mãn độ dài không
        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            // Ném exception nếu mật khẩu không hợp lệ
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
        Role userRole = roleRepository.findByRoleId(user.getRole().getRoleId())
                .orElseGet(() -> roleRepository.findByRoleName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Role not found")));
        user.setRole(userRole);
        userRepository.save(user);
        if(validateUserAndRole(user.getUsername(), user.getPassword(),  user.getRole().getRoleName())) {
            encryptAllPasswords();
        }
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }
    public void registerOwnerUser(User user) {
        user.setRole(roleRepository.findByRoleName("OWNER").orElse(null));
        userRepository.save(user);
    }
    @Transactional(readOnly = true) // Đảm bảo không thay đổi dữ liệu khi truy vấn
    public User getCurrentUser() {
        // Lấy Authentication từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu người dùng đã đăng nhập và đã được xác thực
        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy tên người dùng (username) từ Authentication
            String username = authentication.getName();

            // Truy vấn người dùng từ cơ sở dữ liệu
            return userRepository.findByUsername(username).orElse(null);
        }

        // Trả về null nếu người dùng không đăng nhập hoặc không xác thực
        return null;
    }
    public boolean validateUserAndRole(String username, String password, String selectedRole) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Kiểm tra xem mật khẩu đã được mã hóa chưa
            if (!isPasswordEncrypted(user.getPassword())) {

                // Nếu chưa mã hóa, mã hóa và cập nhật lại trong cơ sở dữ liệu
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }

            // Kiểm tra mật khẩu và vai trò
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

            // So sánh vai trò của người dùng với vai trò đã chọn (chỉ có 1 vai trò)
            boolean hasRole = user.getRole().getRoleName().equalsIgnoreCase("ROLE_" + selectedRole);

            return passwordMatches && hasRole;
        }
        return false;
    }


    private boolean isPasswordEncrypted(String password) {
        return password != null && password.startsWith("$2a$");
    }

    public void encryptAllPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if(!isPasswordEncrypted(user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }
        }
    }
}
