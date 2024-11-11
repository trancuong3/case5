package org.example.profilecase5.Service;

import org.example.profilecase5.Exceptions.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exceptions.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
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
        userRepository.save(user);
    }
}
