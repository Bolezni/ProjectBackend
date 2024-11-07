package org.example.testprojectback.repository;

import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Notification;
import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByInvitee(User user);

    boolean existsByInviteeAndGroup(User user, Group group);
    boolean existsByInviterAndInviteeAndGroup(User inviter, User user, Group group);

    @Query("SELECT n FROM Notification n WHERE n.invitee.id = :userId")
    List<Notification> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.invitee.id = :userId AND n.group.id = :groupId")
    void deleteNotificationsByUserIdAndGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

}
