package org.example.projectback.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.projectback.store.entity.enums.Gender;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {

    @NonNull
    private UUID id;

    @JsonProperty("login")
    @NonNull
    private String username;

    @JsonProperty("first_name")
    @NonNull
    private String firstName;

    @JsonProperty("last_name")
    @NonNull
    private String lastName;

    @NonNull
    private Gender gender;

    private String description;

    @JsonProperty("telegram_name")
    @NonNull
    private String tgName;

    private Integer age;
}
