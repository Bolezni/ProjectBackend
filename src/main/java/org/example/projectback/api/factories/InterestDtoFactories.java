package org.example.projectback.api.factories;

import org.example.projectback.api.dto.InterestDto;
import org.example.projectback.store.entity.InterestEntity;
import org.springframework.stereotype.Component;

@Component
public class InterestDtoFactories {

    public InterestDto makeInterestDto(InterestEntity interestEntity) {
        return InterestDto.builder()
                .id(interestEntity.getId())
                .name(interestEntity.getName())
                .build();
    }
}
