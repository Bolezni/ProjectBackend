package org.example.projectback.api.factories;

import lombok.RequiredArgsConstructor;
import org.example.projectback.api.dto.GroupDto;
import org.example.projectback.store.entity.GroupEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupDtoFactories {

    private final UserDtoFactories userDtoFactories;

    private final InterestDtoFactories interestDtoFactories;

    public GroupDto makeGroupDto(GroupEntity groupEntity) {
        return GroupDto.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .chars(groupEntity.getChars())
                .color(groupEntity.getColor())
                .description(groupEntity.getDescription())
                .users(groupEntity.getUsers()
                        .stream()
                        .map(userDtoFactories::makeUserDto)
                        .collect(Collectors.toList()))
                .interests(groupEntity.getInterests()
                        .stream()
                        .map(interestDtoFactories::makeInterestDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
