package org.example.projectback.api.dto;

import lombok.*;
import org.example.projectback.store.entity.InterestEntity;
import org.example.projectback.store.entity.UserEntity;

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
    private List<UserEntity> users;

    @NonNull
    private List<InterestEntity> interests;
}
