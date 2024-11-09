package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.UserDtoUpdate;
import org.example.testprojectback.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDtoUpdatesMapper{

    private final InterestDtoMapper interestDtoMapper;

    public UserDtoUpdate toDto(User user) {
        return new UserDtoUpdate(
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getGender(),
                user.getTgName(),
                user.getDescription(),
                user.getBirthDay(),
                user.getInterests()
                        .stream()
                        .map(interestDtoMapper::toDto)
                        .collect(Collectors.toSet())

        );
    }

    public User toEntity(UserDtoUpdate dto) {
        return User.builder()
                .firstName(dto.firstname())
                .lastName(dto.lastname())
                .patronymic(dto.patronymic())
                .gender(dto.gender())
                .tgName(dto.tgName())
                .description(dto.description())
                .birthDay(dto.birthDay())
                .build();
    }
}
