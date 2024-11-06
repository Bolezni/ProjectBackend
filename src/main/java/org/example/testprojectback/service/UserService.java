package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.email.DefaultEmailService;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.mapper.InterestDtoMapper;
import org.example.testprojectback.mapper.UserDtoMapper;
import org.example.testprojectback.mapper.UserDtoResponseMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.UserRepository;
import org.example.testprojectback.sercurity.RefreshTokenDto;
import org.example.testprojectback.sercurity.jwt.JwtAuthDto;
import org.example.testprojectback.sercurity.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    private final InterestDtoMapper interestDtoMapper;
    private final UserDtoResponseMapper userDtoResponseMapper;
    private final GroupDtoMapper groupDtoMapper;
    private final GroupRepository groupRepository;
    private final DefaultEmailService defaultEmailService;

    @Transactional
    public void addUser(UserDto userDto) {

        if(userRepository.existsByEmail(userDto.email())){
            throw new IllegalArgumentException(
                    "email already taken"
            );
        }
        if(userRepository.existsByUsername(userDto.username())){
            throw new IllegalArgumentException("username already taken");
        }

        if(userDto.password().length() < 6){
            throw new IllegalArgumentException("password must be at least 6 characters");
        }

        User user = User.builder()
                .username(userDto.username())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstname())
                .lastName(userDto.lastname())
                .patronymic(userDto.patronymic())
                .email(userDto.email())
                .birthDay(userDto.birthDay())
                .description(userDto.description())
                .tgName(userDto.tgName())
                .gender(userDto.gender())
                .isAdmin(userDto.isAdmin())
                .profileImageId(userDto.profileImageId())
                .build();

        String activatedCode = generateSecurityCode();
        user.setActivateCode(activatedCode);

        userRepository.saveAndFlush(user);

        defaultEmailService.sendConfirmationEmail(userDto.email(), activatedCode);
    }

    public JwtAuthDto singIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);


        return jwtService.generateAuthToken(user.getUsername());
    }


    public JwtAuthDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByUsername(jwtService.getUserNameFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getUsername(), refreshToken);
        }
        throw new  AuthenticationException("Invalid refresh token");
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUsername(userCredentialsDto.username());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.password(), user.getPassword())){
                return user;
            }
        }
        throw new AuthenticationException("Email or password is not correct");
    }

    private User findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username).orElseThrow(()->
                new Exception(String.format("User with login %s not found", username)));
    }

    private String generateSecurityCode(){
        return UUID.randomUUID().toString().replace("-", "").substring(0,6).toUpperCase();
    }

    @Transactional
    public void delete(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("customer with login [%s] not found".formatted(username)));

        userRepository.delete(user);
    }

    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }


    @Transactional
    public void updateUserProfile(String userName, UserDtoUpdate userDtoUpdate) {

        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User already exist"));

        if(userDtoUpdate.birthDay() != null &&
                !userDtoUpdate.birthDay().isAfter(LocalDate.of(1970, 1,1)) &&
                !userDtoUpdate.birthDay().isBefore(LocalDate.now()) ){
            throw new IllegalArgumentException("birthday must be after birthday");
        }

        user.setFirstName(userDtoUpdate.firstname());
        user.setLastName(userDtoUpdate.lastname());
        user.setPatronymic(userDtoUpdate.patronymic());
        user.setBirthDay(userDtoUpdate.birthDay());
        user.setGender(userDtoUpdate.gender());
        user.setDescription(userDtoUpdate.description());
        user.setTgName(userDtoUpdate.tgName());

        if(userDtoUpdate.interests() != null){

            Set<String> interestNames = userDtoUpdate.interests()
                    .stream()
                    .map(InterestDto::name)
                    .collect(Collectors.toSet());

            Set<Interest> interests = new HashSet<>(interestRepository.findAllByNameIn(interestNames));

            user.getInterests().addAll(interests);
        }


        userRepository.saveAndFlush(user);
    }




    @Transactional
    public void removeInterestsFromUser(String userName, Set<InterestDto> interests) {

        if (userName == null || userName.isEmpty() || interests == null || interests.isEmpty()) {
            throw new IllegalArgumentException("User  name and interests must not be null or empty");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> interestNames = interests.stream()
                .map(InterestDto::name)
                .collect(Collectors.toSet());

        user.setInterests(user.getInterests().stream()
                .filter(interest -> !interestNames.contains(interest.getName()))
                .collect(Collectors.toSet()));

        // Сохраняем изменения
        userRepository.save(user);
    }

    @Transactional
    public void removeInterestFromUser (String userName, String interestName) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        Interest interest = interestRepository
                .findByName(interestName)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        user.getInterests().remove(interest);

        userRepository.saveAndFlush(user);
    }


    @Transactional
    public UserDtoResponse getUserDtoByUserName(String userName) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        return userDtoResponseMapper.toDto(user);
    }
    @Transactional
    public void updateSecurityUserData(String userName,String newUserName, String newPassword, String newEmail) {

        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        validateUsername(newUserName);
        if (!user.getUsername().equals(newUserName) && userRepository.existsByUsername(newUserName)) {
            throw new RuntimeException("Username already exists");
        }

        validateEmail(newEmail);
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already exists");
        }
        if (newPassword == null && newPassword.trim().length() < 6 && !newPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        user.setUsername(newUserName);
        user.setEmail(newEmail);
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public Set<InterestDto> getInterests(String userName) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        Set<Interest> interests = user.getInterests();

        return interests.stream()
                .map(interestDtoMapper::toDto)
                .collect(Collectors.toSet());
    }

    public List<UserDto> fetchUserByFullName(String firstName, String lastName, String patronymic) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> (firstName == null || firstName.isEmpty() || user.getFirstName().toLowerCase().startsWith(firstName.toLowerCase())) &&
                        (lastName == null || lastName.isEmpty() || user.getLastName().toLowerCase().startsWith(lastName.toLowerCase())) &&
                        (patronymic == null || patronymic.isEmpty() || user.getPatronymic().toLowerCase().startsWith(patronymic.toLowerCase())))
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> fetchUserByInterest(Set<InterestDto> interests) {
        if (interests == null || interests.isEmpty()) {
            return List.of();
        }

        Set<String> interestNames = interests.stream()
                .map(InterestDto::name)
                .collect(Collectors.toSet());

        List<User> users = userRepository.findByInterestsNameIn(interestNames);

        return users.stream()
                .map(userDtoMapper::toDto)
                .distinct()
                .collect(Collectors.toList());
    }

    public UserDto fetchUserByUserName(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User  not found"));

        return userDtoMapper.toDto(user);
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }


    public void uploadProfileImage(String userName, String profileImageId) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(()-> new RuntimeException("User  not found"));

        if(profileImageId == null || profileImageId.isEmpty()){
            throw new RuntimeException("Profile image id cannot be null or empty");
        }

        if (user.getProfileImageId() != null && user.getProfileImageId().equals(profileImageId)) {
            throw new RuntimeException("Profile image already exists");
        }


        user.setProfileImageId(profileImageId);

        userRepository.saveAndFlush(user);
    }

    public boolean activateUser(String code) {
        User user = userRepository
                .findUserByActivateCode(code)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        if(user != null){
            user.setActivated(true);
            user.setActivateCode(null);
            userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }
    @Transactional
    public Set<GroupDto> getUserSubscribedGroups(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        return user.getSubscribedGroups().stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toSet());
    }
    @Transactional
    public void unSubscribeGroup(String userName, Long groupId) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group  not found"));

        group.getSubscribers().remove(user);

        userRepository.saveAndFlush(user);

        groupRepository.saveAndFlush(group);
    }

    public void subscribeToGroup(String username, Long groupId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if(user.getSubscribedGroups().contains(group)){
            throw new RuntimeException("Group  already exists");
        }else {
            user.getSubscribedGroups().add(group);
        }


        if(group.getSubscribers().contains(user)){
            throw new RuntimeException("User  already exists");
        }else{
            group.getSubscribers().add(user);
        }


        userRepository.saveAndFlush(user);

        groupRepository.saveAndFlush(group);

    }
    @Transactional
    public void addInterestsToUser(String username, Set<String> interests) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (interests == null || interests.isEmpty()) {
            throw new IllegalArgumentException("Members set cannot be null");
        }

        Set<Interest> interestSet = new HashSet<>(interestRepository.findAllByNameIn(interests));


        user.getInterests().addAll(interestSet);

        userRepository.saveAndFlush(user);
    }
}
