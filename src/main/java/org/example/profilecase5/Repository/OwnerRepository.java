package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.Owner;
import org.example.profilecase5.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    boolean existsByUser(User user);
}