package org.example.profilecase5.Service;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RentalHistoryService {
    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;

    public void save(RentalHistory rentalHistory) {
        rentalHistoryRepository.save(rentalHistory);
    }

}
