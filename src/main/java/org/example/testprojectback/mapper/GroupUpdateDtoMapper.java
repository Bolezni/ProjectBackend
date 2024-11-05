package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupUpdateDto;
import org.example.testprojectback.model.Group;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupUpdateDtoMapper {

    public GroupUpdateDto toDto(Group group) {
        return new GroupUpdateDto(
                group.getChars(),
                group.getName(),
                group.getColor(),
                group.getDescription()
        );
    }

    public Group toEntity(GroupUpdateDto dto) {
        return Group.builder()
                .chars(dto.chars())
                .name(dto.name())
                .color(dto.color())
                .description(dto.description())
                .build();
    }
}
