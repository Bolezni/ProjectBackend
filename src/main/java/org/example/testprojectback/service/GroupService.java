package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.mapper.NotificationDtoMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.Notification;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.NotificationRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final GroupDtoMapper groupDtoMapper;
    private final InterestRepository interestRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDtoMapper notificationDtoMapper;

    @Transactional
    public void createGroup(String userName, GroupDto groupDto) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = Group.builder()
                .name(groupDto.name())
                .chars(groupDto.chars())
                .color(groupDto.color())
                .creator(user)
                .description(groupDto.description())
                .build();

        if(groupDto.interests() != null && !groupDto.interests().isEmpty()) {

            Set<String> interestsName = groupDto.interests()
                    .stream()
                    .map(InterestDto::name)
                    .collect(Collectors.toSet());

            Set<Interest> interests = interestsName
                    .stream()
                    .map(it -> interestRepository.findByName(it)
                            .orElseThrow(() -> new RuntimeException("Interest not found")))
                    .collect(Collectors.toSet());

            if(interests.isEmpty()) {
                throw new RuntimeException("interests not found");
            }

            group.getInterests().addAll(interests);
        }

        if(groupDto.subscribers() != null && !groupDto.subscribers().isEmpty()) {
            Set<String> subscriberString = groupDto.subscribers().stream()
                    .map(UserDto::username)
                    .collect(Collectors.toSet());

            Set<User> users = subscriberString
                    .stream()
                    .map(it -> userRepository.findByUsername(it)
                            .orElseThrow(() -> new RuntimeException("Interest not found")))
                    .collect(Collectors.toSet());

            if(users.isEmpty()) {
                throw new RuntimeException("Users not found");
            }

            group.getSubscribers().addAll(users);
        }



        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public void addInterestToGroup(Long groupId, Set<String> interestsName) {
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Set<Interest> interests = new HashSet<>(interestRepository.findAllByNameIn(interestsName));

        group.getInterests().addAll(interests);

        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public NotificationDto inviteUserToGroup(Long groupId, String userName) {
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User userSave = userRepository
                .findByUsername(userName)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Notification notification = notificationService.createNotification(userSave.getUsername(), group.getId(),"");

        return notificationDtoMapper.toDto(notification);
    }

//    @Transactional
//    public void addUserToGroup(Long groupId, String userName) {
//        Group group = groupRepository
//                .findById(groupId)
//                .orElseThrow(() -> new RuntimeException("Group not found"));
//
//        User userSave = userRepository
//                .findByUsername(userName)
//                .orElseThrow(()-> new RuntimeException("User not found"));
//
//        group.getSubscribers().add(userSave);
//
//        groupRepository.saveAndFlush(group);
//    }


    @Transactional
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void addUsersToGroup(Long groupId, Set<User> members) {
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (members == null) {
            throw new IllegalArgumentException("Members set cannot be null");
        }

        members.stream()
                .map(user -> {
                    String message = "Вас приглашают в группу " + group.getName();
                    return notificationService.createNotification(user.getUsername(), group.getId(), message);
                })
                .collect(Collectors.toList());

        group.getSubscribers().addAll(members);

        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public List<GroupDto> fetchGroupsByName(Optional<String> optionalPrefixName) {
        if (optionalPrefixName.isPresent() && !optionalPrefixName.get().isEmpty()) {

            List<Group> groups = groupRepository.findByNameStartingWith(optionalPrefixName.get());

            return groups.stream()
                    .map(groupDtoMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return groupRepository.findAll().stream()
                    .map(groupDtoMapper::toDto)
                    .collect(Collectors.toList());
        }

    }
    @Transactional
    public List<GroupDto> fetchGroupsByInterest(Set<InterestDto> interests) {
        if (interests == null || interests.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> interestNames = interests.stream()
                .map(InterestDto::name)
                .collect(Collectors.toSet());

        Set<Interest> interestSet = interestRepository.findByNameIn(interestNames);

        List<Group> groups = groupRepository.findByInterestsIn(interestSet);

        return groups.stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteGroup(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        groupRepository.deleteById(groupId);
    }

    @Transactional
    public void acceptGroupInvitation(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.isAccepted()) {
            throw new RuntimeException("Invitation already accepted");
        }

        User user = notification.getUser();
        Group group = notification.getGroup();

        if(user.getSubscribedGroups().contains(group)) {
            throw new RuntimeException("Invitation already accepted");
        }
        else {
            user.getSubscribedGroups().add(group);
        }

        if(group.getSubscribers().contains(user)) {
            throw new RuntimeException("Invitation already accepted");
        }else {
            group.getSubscribers().add(user);
        }


        groupRepository.saveAndFlush(group);

        userRepository.saveAndFlush(user);

        notificationService.acceptNotification(notificationId);
    }

    @Transactional
    public void cancelGroupInvitation(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        User user = notification.getUser();
        Group group = notification.getGroup();

        user.getSubscribedGroups().remove(group);
        group.getSubscribers().remove(user);

        groupRepository.saveAndFlush(group);

        userRepository.saveAndFlush(user);

        notificationService.cancelNotification(notificationId);
    }

    public GroupDto getGroupById(Long groupID) {
        Group group = groupRepository.findById(groupID)
                .orElseThrow(() -> new RuntimeException("Group not exist"));

        return groupDtoMapper.toDto(group);
    }

    public void updateGroup(Long groupId, GroupUpdateDto groupUpdateDto){

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.setName(groupUpdateDto.name());
        group.setColor(groupUpdateDto.color());
        group.setDescription(groupUpdateDto.description());

        groupRepository.saveAndFlush(group);
    }
}
