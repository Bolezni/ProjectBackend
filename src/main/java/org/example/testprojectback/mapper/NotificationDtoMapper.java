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
                userDtoMapper.toDto(notification.getUser()),
                groupDtoMapper.toDto( notification.getGroup()),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }

    public Notification toEntity(NotificationDto notificationDto) {
        return  new Notification(
                userDtoMapper.toEntity(notificationDto.user()),
                groupDtoMapper.toEntity(notificationDto.group()),
                notificationDto.message()
        );
    }
}
