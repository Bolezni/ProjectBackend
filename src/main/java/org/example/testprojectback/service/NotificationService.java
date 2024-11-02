package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Notification;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.NotificationRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Notification createNotification(String userName, Long groupId, String message) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Notification notification = Notification.builder()
                .user(user)
                .group(group)
                .message(message)
                .build();

        notification =  notificationRepository.save(notification);

        return notification;
    }
    @Transactional
    public void acceptNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setAccepted(true);
        notificationRepository.save(notification);
    }
    @Transactional
    public void cancelNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notificationRepository.delete(notification);
    }

}
