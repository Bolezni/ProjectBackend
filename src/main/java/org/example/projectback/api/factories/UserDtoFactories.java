package org.example.projectback.api.factories;

import lombok.RequiredArgsConstructor;
import org.example.projectback.api.dto.UserDto;
import org.example.projectback.store.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDtoFactories {

    private final FriendDtoFactories friendDtoFactories;

    public UserDto makeUserDto(UserEntity user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymic())
                .age(user.getAge())
                .birthDay(user.getBirthDay())
                .description(user.getDescription())
                .tgName(user.getTgName())
                .gender(user.getGender())
                .isAdmin(user.isAdmin())
                .friends(user.getFriends()
                        .stream()
                        .map(friendDtoFactories::makeFriendDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
//блять