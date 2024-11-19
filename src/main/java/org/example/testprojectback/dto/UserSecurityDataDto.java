package org.example.testprojectback.dto;

import javax.validation.constraints.Size;

public record UserSecurityDataDto(
        String newUserName,
        String oldPassword,
        @Size(min = 6, message = "Пароль должен содержать как минимум 6 символов")
        String newPassword
) {
}
