package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
    List<House> findByUser_UserId(int userId);
    List<House> findByBedroomsGreaterThanEqualAndBathroomsGreaterThanEqualAndAddressContainingAndPricePerDayBetween(
            int minBedrooms, int minBathrooms, String address, double minPrice, double maxPrice);
}

