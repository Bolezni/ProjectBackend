package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.FriendShipDto;
import org.example.testprojectback.model.Friendship;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendShipDtoMapper {

    private final UserDtoMapper userDtoMapper;

    public FriendShipDto toDto(Friendship friendship) {
        return new FriendShipDto(
                userDtoMapper.toDto(friendship.getUser()),
                userDtoMapper.toDto(friendship.getFriend()),
                friendship.getStatus(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt()
        );
    }

    public Friendship toEntity(FriendShipDto friendShipDto) {
        return new Friendship(
                userDtoMapper.toEntity(friendShipDto.user()),
                userDtoMapper.toEntity(friendShipDto.friend()),
                friendShipDto.status(),
                friendShipDto.createdAt(),
                friendShipDto.updatedAt()
        );
    }
}
