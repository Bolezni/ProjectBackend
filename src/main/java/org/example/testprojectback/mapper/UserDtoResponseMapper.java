package org.example.testprojectback.mapper;

import org.example.testprojectback.dto.UserDtoResponse;
import org.example.testprojectback.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoResponseMapper {

    public UserDtoResponse toDto(User user) {
        return new UserDtoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getGender(),
                user.getEmail(),
                user.getTgName(),
                user.getProfileImageId(),
                user.getDescription(),
                user.isAdmin(),
                user.getAge(),
                user.getBirthDay()
        );
    }

    public User toEntity(UserDtoResponse dto) {
        return User.builder()
                .username(dto.username())
                .firstName(dto.firstname())
                .lastName(dto.lastname())
                .patronymic(dto.patronymic())
                .email(dto.email())
                .tgName(dto.tgName())
                .profileImageId(dto.profileImageId())
                .description(dto.description())
                .age(dto.age())
                .gender(dto.gender())
                .build();
    }
}
