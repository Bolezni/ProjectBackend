package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.controller.helper.ControllerHelper;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.exceptions.GroupNotFoundException;
import org.example.testprojectback.exceptions.ResourceNotFoundException;
import org.example.testprojectback.exceptions.UserNotFoundException;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ControllerHelper controllerHelper;

    @Transactional
    public void createGroup(String userName, GroupCreateDto groupDto) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
                            .orElseThrow(() -> new ResourceNotFoundException("Interest not found")))
                    .collect(Collectors.toSet());

            if(interests.isEmpty()) {
                throw new ResourceNotFoundException("interests not found");
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
                            .orElseThrow(() -> new ResourceNotFoundException("Interest not found")))
                    .collect(Collectors.toSet());

            if(users.isEmpty()) {
                throw new UserNotFoundException("Users not found");
            }

            group.getSubscribers().addAll(users);
        }

        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public void addInterestToGroup(Long groupId, Set<String> interestsName) {
        Group group = groupRepository
                .findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Set<Interest> interests = new HashSet<>(interestRepository.findAllByNameIn(interestsName));

        group.getInterests().addAll(interests);

        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public Page<GroupDto> getAllGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return groupRepository.findAll(pageable).map(groupDtoMapper::toDto);
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
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        groupRepository.deleteById(groupId);
    }

    public GroupDto getGroupById(Long groupID) {
        Group group = groupRepository.findById(groupID)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        return groupDtoMapper.toDto(group);
    }

    @Transactional
    public void updateGroup(Long groupId, GroupUpdateDto groupUpdateDto){

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Optional.ofNullable(groupUpdateDto.chars())
                .filter(chars -> !chars.isEmpty())
                .ifPresent(group::setChars);

        Optional.ofNullable(groupUpdateDto.color())
                .filter(color -> !color.isEmpty())
                .ifPresent(group::setColor);

        Optional.ofNullable(groupUpdateDto.name())
                .filter(name -> !name.isEmpty())
                .ifPresent(group::setName);

        Optional.ofNullable(groupUpdateDto.description())
                .filter(description -> !description.isEmpty())
                .ifPresent(group::setDescription);

        controllerHelper.switchInterests(group, groupUpdateDto.interests());

        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public Map<String, List<GroupDto>> getTopGroupsGroupedByInterest(int maxGroupsPerInterest) {
        List<Group> groupsData = groupRepository.findAll();

        Map<String, List<Group>> groupedByInterest = groupsData.stream()
                .flatMap(group -> group.getInterests().stream()
                        .map(interest -> new AbstractMap.SimpleEntry<>(interest.getName(), group)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return groupedByInterest.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparingInt((Group group) -> Optional.ofNullable(group.getSubscribers())
                                                .map(Set::size)
                                                .orElse(0))
                                        .reversed())
                                .limit(maxGroupsPerInterest)
                                .map(groupDtoMapper::toDto)
                                .collect(Collectors.toList()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

}
