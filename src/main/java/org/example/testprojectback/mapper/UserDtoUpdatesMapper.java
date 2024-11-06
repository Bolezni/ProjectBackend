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
        return new User(
                dto.firstname(),
                dto.lastname(),
                dto.patronymic(),
                dto.gender(),
                dto.description(),
                dto.tgName(),
                dto.birthDay()
        );
    }
}
