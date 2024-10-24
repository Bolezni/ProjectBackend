package org.example.projectback.api.controller.helper;

import lombok.RequiredArgsConstructor;
import org.example.projectback.api.exception.NotFoundException;
import org.example.projectback.store.entity.UserEntity;
import org.example.projectback.store.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ControllerHelper {

    private final UserRepository userRepository;

    public UserEntity getUserEntityOrThrowException(UUID id) {
       return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
