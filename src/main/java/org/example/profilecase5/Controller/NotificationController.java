package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.RentalNotificationDTO;
import org.example.profilecase5.Service.RentalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")

public class NotificationController {

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @GetMapping("")
    public ResponseEntity<List<RentalNotificationDTO>> getNotifications() {
        List<RentalNotificationDTO> notifications = rentalHistoryService.getLatestNotifications();
        return ResponseEntity.ok(notifications);
    }

}
