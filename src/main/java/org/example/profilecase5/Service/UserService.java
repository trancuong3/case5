package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + userId));
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void updateUserAvatar(String username, String avatarPath) {
        // Sử dụng Optional để lấy user từ database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với username: " + username));

        // Cập nhật đường dẫn avatar và lưu lại
        user.setAvatar(avatarPath);
        userRepository.save(user);
    }
    public User getCurrentUser() {
        // Lấy thông tin user hiện tại
        // chưa làm security
        return userRepository.findById(2).orElse(null);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
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
        userRepository.save(user);
    }





}
