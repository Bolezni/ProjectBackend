package org.example.testprojectback.repository;

import org.example.testprojectback.model.Notification;
import org.example.testprojectback.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
