package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.mapper.InterestDtoMapper;
import org.example.testprojectback.mapper.UserDtoMapper;
import org.example.testprojectback.mapper.UserDtoResponseMapper;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    private final String UPLOAD_DIR = "uploads/";
    private final InterestDtoMapper interestDtoMapper;
    private final UserDtoResponseMapper userDtoResponseMapper;

    @Transactional
    public void addUser(UserDto userDto) {
        String email = userDto.email();

        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException(
                    "email already taken"
            );
        }
        if(userRepository.existsByUsername(userDto.userName())){
            throw new IllegalArgumentException("username already taken");
        }

        if(userDto.password().length() < 6){
            throw new IllegalArgumentException("password must be at least 6 characters");
        }

        User user = User.builder()
                .username(userDto.userName())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .patronymic(userDto.patronymic())
                .email(email)
                .birthDay(userDto.birthDay())
                .description(userDto.description())
                .tgName(userDto.tgName())
                .gender(userDto.gender())
                .isAdmin(false)
                .profileImageId(userDto.profileImageId())
                .build();

        String activatedCode = UUID.randomUUID().toString();
        user.setActivateCode(activatedCode);

        userRepository.saveAndFlush(user);


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
                .orElseThrow(() -> new RuntimeException("User  not found"));

        if(userDtoUpdate.birthDay() != null &&
                !userDtoUpdate.birthDay().isAfter(LocalDate.of(1970, 1,1)) &&
                !userDtoUpdate.birthDay().isBefore(LocalDate.now()) ){
            throw new IllegalArgumentException("birthday must be after birthday");
        }

        user.setFirstName(userDtoUpdate.firstName());
        user.setLastName(userDtoUpdate.lastName());
        user.setPatronymic(userDtoUpdate.patronymic());
        user.setBirthDay(user.getBirthDay());
        user.setGender(user.getGender());
        user.setDescription(userDtoUpdate.description());
        user.setTgName(userDtoUpdate.tgName());

        if(userDtoUpdate.interests() != null){

            Set<Interest> interestSet = userDtoUpdate.interests()
                    .stream()
                    .map(interestDtoMapper::toEntity)
                    .collect(Collectors.toSet());

            user.getInterests().addAll(interestSet);
        }


        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void addInterestToUser(String userName, String interestName) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Interest interest = interestRepository
                .findByName(interestName)
                .orElseThrow(() -> new RuntimeException("Interest not found"));


        user.getInterests().add(interest);


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

        validatePassword(newPassword);

        if (!user.getUsername().equals(newUserName)) {
            user.setUsername(newUserName);
        }
        if (!user.getEmail().equals(newEmail)) {
            user.setEmail(newEmail);
        }
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

    private void validatePassword(String password) {
        if (password == null || password.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    public void uploadProfileImage(String userName, String profileImageId) {
        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(()-> new RuntimeException("User  not found"));

        if(user.getProfileImageId().equals(profileImageId)){
            throw new RuntimeException("Profile image already exists");
        }

        if(profileImageId == null || profileImageId.isEmpty()){
            throw new RuntimeException("Profile image id cannot be null or empty");
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
            user.setActivateCode("");
            userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }


    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
