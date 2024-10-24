package org.example.projectback.api.dto;

import lombok.*;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {

    @NonNull
    private Long id;

    @NonNull
    private String chars;

    @NonNull
    private String name;

    @NonNull
    private String color;

    private String description;

    @NonNull
    private List<UserDto> users;

    @NonNull
    private List<InterestDto> interests;
}
