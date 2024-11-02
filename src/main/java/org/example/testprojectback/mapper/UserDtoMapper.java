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
                user.getBirthDay()
        );
    }


    public User toEntity(UserDto userDto){
        return new User(
                userDto.userName(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.patronymic(),
                userDto.password(),
                userDto.email(),
                userDto.gender(),
                userDto.description(),
                userDto.tgName(),
                userDto.profileImageId(),
                userDto.birthDay()
        );
    }
}
