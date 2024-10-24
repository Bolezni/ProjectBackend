package org.example.projectback.api.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.projectback.api.controller.helper.ControllerHelper;
import org.example.projectback.api.dto.AckDto;
import org.example.projectback.api.dto.UserDto;
import org.example.projectback.api.dto.UserRegistrationRequest;
import org.example.projectback.api.exception.BadRequestException;
import org.example.projectback.api.exception.DuplicateResourceException;
import org.example.projectback.api.exception.NotFoundException;
import org.example.projectback.api.factories.UserDtoFactories;
import org.example.projectback.store.entity.UserEntity;
import org.example.projectback.store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoFactories userDtoFactories;

    private final ControllerHelper controllerHelper;

    @Transactional
    public UserDto getByUserName(String login) {
        UserEntity userEntity = userRepository
                .findByUsername(login)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userDtoFactories.makeUserDto(userEntity);
    }

    @Transactional
    public UserDto createUser(UserRegistrationRequest userRegistrationRequest){
        String email = userRegistrationRequest.email();

        if(userRepository.existsByEmail(email)){
            throw new DuplicateResourceException("User already exist");
        }

        if(userRegistrationRequest.password().isBlank()){
            throw new BadRequestException("Password cannot be blank");
        }

        if(userRegistrationRequest.firstName().isBlank() || userRegistrationRequest.lastName().isBlank()){
            throw new BadRequestException("The first or last name is empty");
        }

        if(userRegistrationRequest.birthDate() == null || userRegistrationRequest.birthDate().isAfter(LocalDate.now()) || userRegistrationRequest.birthDate().isBefore(LocalDate.of(1945,1,1))){
            throw new BadRequestException("The birth date is invalid");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(userRegistrationRequest.username())
                .password(userRegistrationRequest.password())
                .email(userRegistrationRequest.email())
                .firstName(userRegistrationRequest.firstName())
                .lastName(userRegistrationRequest.lastName())
                .birthDay(userRegistrationRequest.birthDate())
                .gender(userRegistrationRequest.gender())
                .tgName(userRegistrationRequest.tgName())
                .build();

        userEntity = userRepository.saveAndFlush(userEntity);

        return userDtoFactories.makeUserDto(userEntity);
    }

    public UserDto updateUserDto(UUID id,@NonNull UserDto userDto){

        //TODO: сделать обновление пользователя
        return null;

    }

    @Transactional
    public AckDto delete(UUID id){
        UserEntity user = controllerHelper.getUserEntityOrThrowException(id);

        userRepository.delete(user);

        return AckDto.builder()
                .answer("User deleted")
                .build();
    }

}
