package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.NotificationDto;
import org.example.testprojectback.model.Notification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDtoMapper {

    private final UserDtoMapper userDtoMapper;
    private final GroupDtoMapper groupDtoMapper;

    public NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                userDtoMapper.toDto(notification.getInviter()),
                userDtoMapper.toDto(notification.getInvitee()),
                groupDtoMapper.toDto( notification.getGroup()),
                notification.getCreatedAt()
        );
    }

    public Notification toEntity(NotificationDto notificationDto) {
        return  Notification.builder()
                .invitee(userDtoMapper.toEntity(notificationDto.invitee()))
                .inviter(userDtoMapper.toEntity(notificationDto.inviter()))
                .group(groupDtoMapper.toEntity(notificationDto.group()))
                .id(notificationDto.id())
                .createdAt(notificationDto.createdAt())
                .build();
    }
}
