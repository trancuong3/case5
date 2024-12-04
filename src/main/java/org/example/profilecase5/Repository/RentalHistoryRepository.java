package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.RentalNotificationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
    @Query("SELECT new org.example.profilecase5.Model.RentalNotificationDTO(r.user.username, r.house.propertyName, r.startDate) " +
            "FROM RentalHistory r ORDER BY r.startDate DESC")
    List<RentalNotificationDTO> findLatestNotifications();
}
