package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.NotificationDto;
import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private static final String ACCEPT = "/accept";
    private static final String CANCEL = "/cancel";
    private static final String GET_BY_ID = "/{id}";
    private static final String AVAILABLE = "/available-invitations";

    @PostMapping(ACCEPT)
    public ResponseEntity<?> acceptInvitation(@RequestParam Long notificationId) {
        notificationService.acceptNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(CANCEL)
    public ResponseEntity<?> cancelInvitation(@RequestParam Long notificationId) {
        notificationService.cancelNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotificationsWhereStatusPending(@RequestParam String username) {
        List<NotificationDto> list = notificationService.getAllNotificationsByUsername(username);

        return ResponseEntity.ok().body(list);
    }

    @GetMapping(GET_BY_ID)
    public NotificationDto getNotificationById(@PathVariable(name = "id") Long notificationId) {
        return notificationService.getInvitation(notificationId);
    }

    @GetMapping(AVAILABLE)
    public ResponseEntity<List<UserDto>> getAvailableUsersForInvitation(@RequestParam Long groupId,
                                                                        @RequestParam(name = "login") String username) {

        List<UserDto> availableUsers = notificationService.getAvailableUsersForInvitation(groupId,username);

        return ResponseEntity.ok().body(availableUsers);
    }

}