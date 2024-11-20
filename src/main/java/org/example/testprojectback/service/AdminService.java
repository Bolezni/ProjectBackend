package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupDto;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.exceptions.ResourceNotFoundException;
import org.example.testprojectback.exceptions.UserNotFoundException;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.mapper.InterestDtoMapper;
import org.example.testprojectback.mapper.UserDtoMapper;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.UserRepository;
import org.example.testprojectback.repository.intermediate.GroupInterestRepository;
import org.example.testprojectback.repository.intermediate.GroupUserRepository;
import org.example.testprojectback.repository.intermediate.UserGroupRepository;
import org.example.testprojectback.repository.intermediate.UserInterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    private final InterestRepository interestRepository;

    private final GroupRepository groupRepository;

    private final InterestDtoMapper interestDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final GroupDtoMapper groupDtoMapper;
    private final UserInterestRepository userInterestRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupInterestRepository groupInterestRepository;
    private final UserGroupRepository userGroupRepository;

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<InterestDto> getAllInterests() {
        return interestRepository.findAll()
                .stream()
                .map(interestDtoMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public InterestDto addInterest(String name,String color) {
        Interest interestSaved = Interest.builder()
                .name(name)
                .color(color)
                .build();

        interestSaved = interestRepository.saveAndFlush(interestSaved);

        return interestDtoMapper.toDto(interestSaved);
    }

    @Transactional
    public void deleteGroupById(Long groupId) {
        groupRepository.findById(groupId)
                        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        groupUserRepository.removeGroupFromUsers(groupId);

        groupInterestRepository.deleteAllByGroupId(groupId);

        userGroupRepository.deleteAllByGroupId(groupId);

        groupRepository.deleteById(groupId);
    }
    @Transactional
    public void deleteInterestByName(String name) {

        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Interest name is null or empty");
        }

        Interest interest = interestRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));

        groupRepository.removeInterestFromGroups(interest.getId());

        userInterestRepository.deleteByInterestId(interest.getId());

        interestRepository.delete(interest);

    }
    @Transactional
    public void deleteUserByUserName(String userName){

        if(userName == null || userName.isEmpty()){
            throw new IllegalArgumentException("Username is null or empty");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        groupRepository.removeUserFromGroups(user.getId());

        userInterestRepository.deleteByUserId(user.getId());

        userRepository.deleteById(user.getId());
    }
}
