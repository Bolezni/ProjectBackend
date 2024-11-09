package org.example.testprojectback.mapper;

import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user){
        return new UserDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getPassword(),
                user.getEmail(),
                user.getGender(),
                user.getTgName(),
                user.getDescription(),
                user.getProfileImageId(),
                user.getAge(),
                user.isAdmin(),
                user.isActivated(),
                user.getBirthDay()
        );
    }


    public User toEntity(UserDto userDto){
        return User.builder()
                .username(userDto.username())
                .firstName(userDto.firstname())
                .lastName(userDto.lastname())
                .patronymic(userDto.patronymic())
                .password(userDto.password())
                .email(userDto.email())
                .gender(userDto.gender())
                .tgName(userDto.tgName())
                .description(userDto.description())
                .profileImageId(userDto.profileImageId())
                .birthDay(userDto.birthDay())
                .isActivated(userDto.activeCode())
                .build();
    }
}
