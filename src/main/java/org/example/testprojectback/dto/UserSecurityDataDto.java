package org.example.testprojectback.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public record UserSecurityDataDto(
        String newUserName,
        @NotEmpty
        @Size(min = 6)
        String oldPassword,
        @Size(min = 6, message = "Пароль должен содержать как минимум 6 символов")
        String newPassword
) {
}
