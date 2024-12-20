package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupDto;
import org.example.testprojectback.model.Group;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupDtoMapper  {

    private final InterestDtoMapper interestDtoMapper;
    private final UserDtoMapper userDtoMapper;

    public GroupDto toDto(Group group) {
        return new GroupDto(
                group.getId(),
                group.getChars(),
                group.getName(),
                group.getColor(),
                group.getDescription(),
                userDtoMapper.toDto(group.getCreator()) ,
                group.getInterests().stream()
                        .map(interestDtoMapper::toDto)
                        .collect(Collectors.toSet()),
                group.getSubscribers().stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toSet())
        );
    }

    public Group toEntity(GroupDto groupDto) {
        return Group.builder()
                .id(groupDto.id())
                .chars(groupDto.chars())
                .name(groupDto.name())
                .color(groupDto.color())
                .description(groupDto.description())
                .creator(userDtoMapper.toEntity(groupDto.creator()))
                .build();
    }
}
