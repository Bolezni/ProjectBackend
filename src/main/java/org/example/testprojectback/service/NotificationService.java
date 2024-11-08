package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.NotificationDto;
import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.mapper.NotificationDtoMapper;
import org.example.testprojectback.mapper.UserDtoMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Notification;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.NotificationRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final NotificationDtoMapper notificationDtoMapper;
    private final UserDtoMapper userDtoMapper;

    @Transactional
    public NotificationDto createNotification(String fromUsername,String toUsername , Long groupId) {
        User userFrom = userRepository
                .findByUsername(fromUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));


        User userTo = userRepository
                .findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if(notificationRepository.existsByInviteeAndGroup(userTo,group)){
            throw new RuntimeException("Notification already exists");
        }

        Notification notification = Notification.builder()
                .invitee(userTo)
                .inviter(userFrom)
                .group(group)
                .build();

        notificationRepository.save(notification);

        return notificationDtoMapper.toDto(notification);
    }
    @Transactional
    public void acceptNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));


        User invitee = notification.getInvitee();
        User inviter = notification.getInviter();

        Group group = notification.getGroup();

        if(invitee.getSubscribedGroups().contains(group)) {
            throw new RuntimeException("Invitation already accepted");
        }
        else {
            invitee.getSubscribedGroups().add(group);
        }

        if(group.getSubscribers().contains(invitee)) {
            throw new RuntimeException("Invitation already accepted");
        }else {
            group.getSubscribers().add(invitee);
        }

        if(!notificationRepository.existsByInviterAndInviteeAndGroup(inviter, invitee,group)) {
            throw new RuntimeException("Invitation already accepted");
        }

        notificationRepository.deleteNotificationsByUserIdAndGroup(invitee.getId(), group.getId());

        groupRepository.saveAndFlush(group);

        userRepository.saveAndFlush(invitee);

        notificationRepository.save(notification);
    }
    @Transactional
    public void cancelNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        User invitee = notification.getInvitee();

        Group group = notification.getGroup();

        invitee.getSubscribedGroups().remove(group);

        group.getSubscribers().remove(invitee);


        groupRepository.saveAndFlush(group);

        userRepository.saveAndFlush(invitee);
    }

    public NotificationDto getInvitation(Long notificationId) {
         Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        return notificationDtoMapper.toDto(notification);
    }

    @Transactional
    public List<NotificationDto> getAllNotificationsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> list = notificationRepository.findByInvitee(user);

        return list.stream()
                .map(notificationDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserDto> getAvailableUsersForInvitation(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long groupIdCreator = group.getCreator().getId();

        List<User> availableUsers = userRepository.findAvailableUsersForInvitation(groupId,user.getId(),groupIdCreator);

        return availableUsers.stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
