package org.example.projectback.api.factories;

import org.example.projectback.api.dto.FriendDto;
import org.example.projectback.store.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class FriendDtoFactories {

    public FriendDto makeFriendDto(UserEntity user) {
        return FriendDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .tgName(user.getTgName())
                .age(user.getAge())
                .build();
    }
}
