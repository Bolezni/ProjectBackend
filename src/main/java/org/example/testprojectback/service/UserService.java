package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.controller.helper.ControllerHelper;
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
import org.example.testprojectback.repository.NotificationRepository;
import org.example.testprojectback.repository.UserRepository;
import org.example.testprojectback.sercurity.RefreshTokenDto;
import org.example.testprojectback.sercurity.jwt.JwtAuthDto;
import org.example.testprojectback.sercurity.jwt.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final NotificationRepository notificationRepository;
    private final ControllerHelper controllerHelper;

    @Transactional
    public void addUser(UserRegisterDto userDto) {

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


    @Transactional
    public void updateUserProfile(String userName, UserDtoUpdate userDtoUpdate) {

        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User already exist"));

        if (userDtoUpdate.birthDay() != null) {
            if (userDtoUpdate.birthDay().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("birthDay must be in the past");
            }
            if (userDtoUpdate.birthDay().isBefore(LocalDate.of(1970, 1, 1))) {
                throw new IllegalArgumentException("birthDay must be after 1970-01-01");
            }
        }

        Optional.ofNullable(userDtoUpdate.firstname())
                .filter(firstname -> !firstname.isEmpty())
                .ifPresent(user::setFirstName);

        Optional.ofNullable(userDtoUpdate.lastname())
                .filter(lastname -> !lastname.isEmpty())
                .ifPresent(user::setLastName);

        Optional.ofNullable(userDtoUpdate.patronymic())
                .filter(patronymic -> !patronymic.isEmpty())
                .ifPresent(user::setPatronymic);

        Optional.ofNullable(userDtoUpdate.birthDay())
                .filter(birthDay -> birthDay.isBefore(LocalDate.now()))
                .filter(birthDay -> birthDay.isAfter(LocalDate.now().minusYears(100)))
                .ifPresent(user::setBirthDay);
        Optional.ofNullable(userDtoUpdate.description())
                .filter(description -> !description.isEmpty())
                .ifPresent(user::setDescription);

        Optional.ofNullable(userDtoUpdate.tgName())
                .filter(tgName -> !tgName.isEmpty())
                .ifPresent(user::setTgName);

        Optional.ofNullable(userDtoUpdate.gender())
                        .ifPresent(user::setGender);

        controllerHelper.switchInterests(user,userDtoUpdate.interests());

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
    public void updateSecurityUserData(String username,UserSecurityDataDto userSecurity) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(userSecurity.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        String newUserName = userSecurity.newUserName();
        if (newUserName != null && !newUserName.isEmpty() && !newUserName.equals(user.getUsername())) {
            if (userRepository.existsByUsername(newUserName)) {
                throw new IllegalArgumentException("Имя пользователя уже существует");
            }
            user.setUsername(newUserName);
        }

        String newPassword = userSecurity.newPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new IllegalArgumentException("Новый пароль совпадает с текущим");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.saveAndFlush(user);

    }


    public Set<InterestDto> getInterests(String username) {
        User user = userRepository
                .findByUsername(username)
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

    public List<UserDto> fetchUserByFullName(UserFetchDto userFetchDto) {
        List<User> users = userRepository.findUsersByFullName(userFetchDto.firstName(), userFetchDto.lastName(), userFetchDto.patronymic());

        return users.stream()
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


    @Transactional
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
    public Page<GroupDto> getUserSubscribedGroups(String userName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Group> subscribedGroups = groupRepository.findSubscribedGroupsByUserName(userName, pageable);
        return subscribedGroups.map(groupDtoMapper::toDto);
    }
    @Transactional
    public void unSubscribeGroup(String userName, Long groupId) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group  not found"));

        group.getSubscribers().remove(user);

        user.getSubscribedGroups().remove(group);

        notificationRepository.deleteNotificationsByUserIdAndGroup(user.getId(), group.getId());

        userRepository.saveAndFlush(user);

        groupRepository.saveAndFlush(group);
    }

    @Transactional
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

    @Transactional
    public Page<GroupDto> getUserCreatedGroups(String userName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Group> groups = groupRepository.findByCreatorUsername(userName, pageable);
        return groups.map(groupDtoMapper::toDto);
    }

}
