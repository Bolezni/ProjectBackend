package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.NotificationDto;
import org.example.testprojectback.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@RequestParam Long notificationId) {
        notificationService.acceptNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelInvitation(@RequestParam Long notificationId) {
        notificationService.cancelNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotificationsWhereStatusPending(@RequestParam String username) {
        List<NotificationDto> list = notificationService.getAllNotificationsByUsername(username);

        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public NotificationDto getNotificationById(@PathVariable(name = "id") Long notificationId) {
        return notificationService.getInvitation(notificationId);
    }

}