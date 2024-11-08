package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupDto;
import org.example.testprojectback.dto.GroupUpdateDto;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final GroupDtoMapper groupDtoMapper;
    private final InterestRepository interestRepository;

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
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toList());
    }

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

    public GroupDto getGroupById(Long groupID) {
        Group group = groupRepository.findById(groupID)
                .orElseThrow(() -> new RuntimeException("Group not exist"));

        return groupDtoMapper.toDto(group);
    }

    @Transactional
    public void updateGroup(Long groupId, GroupUpdateDto groupUpdateDto){

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.setName(groupUpdateDto.name());
        group.setColor(groupUpdateDto.color());
        group.setDescription(groupUpdateDto.description());

        if (groupUpdateDto.interests() != null) {
            Set<String> interestNames = groupUpdateDto.interests()
                    .stream()
                    .map(InterestDto::name)
                    .collect(Collectors.toSet());

            Set<Interest> interests = new HashSet<>(interestRepository.findAllByNameIn(interestNames));

            if (interests.size() != interestNames.size()) {
                throw new IllegalArgumentException("Some interests are not found in the database");
            }

            Set<Interest> currentInterests = group.getInterests();
            Set<Interest> interestsToRemove = new HashSet<>(currentInterests);
            interestsToRemove.removeAll(interests);

            Set<Interest> interestsToAdd = new HashSet<>(interests);
            interestsToAdd.removeAll(currentInterests);

            currentInterests.removeAll(interestsToRemove);
            currentInterests.addAll(interestsToAdd);
        }

        groupRepository.saveAndFlush(group);
    }
}
