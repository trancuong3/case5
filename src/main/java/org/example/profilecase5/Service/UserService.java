package org.example.profilecase5.Service;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List; // Import for List

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Fetch a user by ID
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // Update or save a user
    public void updateUser(User user) {
        userRepository.save(user);
    }

    // Fetch all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
