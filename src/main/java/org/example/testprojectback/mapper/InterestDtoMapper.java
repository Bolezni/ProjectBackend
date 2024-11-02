package org.example.testprojectback.mapper;

import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.model.Interest;
import org.springframework.stereotype.Component;


@Component
public class InterestDtoMapper {
    public InterestDto toDto(Interest interest){
        return new InterestDto(
                interest.getName(),
                interest.getColor()
        );
    }


    public Interest toEntity(InterestDto interestDto){
        return new Interest(
                interestDto.name(),
                interestDto.color()
        );
    }


}
