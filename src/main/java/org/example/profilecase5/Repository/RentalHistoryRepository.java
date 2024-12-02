package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
}
