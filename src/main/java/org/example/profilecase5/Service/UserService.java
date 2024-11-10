package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
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

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // Handle username already exists
            throw new UsernameAlreadyExistsException("Tên người dùng đã tồn tại");
        }
        userRepository.save(user);
    }
}
