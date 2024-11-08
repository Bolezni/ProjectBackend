package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupUpdateDto;
import org.example.testprojectback.model.Group;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupUpdateDtoMapper {

    private final InterestDtoMapper interestDtoMapper;

    public GroupUpdateDto toDto(Group group) {
        return new GroupUpdateDto(
                group.getChars(),
                group.getName(),
                group.getColor(),
                group.getDescription(),
                group.getInterests()
                        .stream()
                        .map(interestDtoMapper::toDto)
                        .collect(Collectors.toSet())
        );
    }

    public Group toEntity(GroupUpdateDto dto) {
        return Group.builder()
                .chars(dto.chars())
                .name(dto.name())
                .color(dto.color())
                .description(dto.description())
                .interests(dto.interests()
                        .stream()
                        .map(interestDtoMapper::toEntity)
                        .collect(Collectors.toSet()))
                .build();
    }
}
