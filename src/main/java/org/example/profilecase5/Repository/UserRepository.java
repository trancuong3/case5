package org.example.profilecase5.Repository;


import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role ownerRole);
    User findByEmail(String email);
}
