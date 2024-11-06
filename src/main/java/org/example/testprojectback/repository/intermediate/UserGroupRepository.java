package org.example.testprojectback.repository.intermediate;

import org.example.testprojectback.model.intermediate.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    void deleteAllByGroupId(Long groupId);
}
