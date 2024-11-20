package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record UserRegisterDto(
        @NotBlank(message = "Логие не может быть пустым")
        String username,
        @NotBlank(message = "Имя не может быть пустым")
        String firstname,
        @NotBlank(message = "Фамилия не может быть пустым")
        String lastname,
        String patronymic,
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, message = "Пароль должен содержать как минимум 6 символов")
        String password,
        @Email(message = "Email не корректен")
        String email,
        @NotBlank(message = "Пол не может быть пустым")
        Gender gender,
        @NotBlank(message = "Установите свой телеграм")
        String tgName,
        String description,
        String profileImageId,
        Integer age,
        boolean activeCode,
        boolean isAdmin,
        LocalDate birthDay
) {
}
