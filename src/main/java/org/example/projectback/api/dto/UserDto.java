package org.example.projectback.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.projectback.store.entity.enums.Gender;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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

    private String patronymic; //отчество

    @NonNull
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NonNull
    private Gender gender;

    private String description;

    @JsonProperty("telegram_name")
    @NonNull
    private String tgName;

    private boolean isAdmin;

    private Integer age;

    @NonNull
    private LocalDate birthDay;

    @NonNull
    private Set<FriendDto> friends;

    @NonNull
    private List<GroupDto> groups;

    @NonNull
    private List<InterestDto> interests;
}
