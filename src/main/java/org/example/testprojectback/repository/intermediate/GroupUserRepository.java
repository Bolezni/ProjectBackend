package org.example.testprojectback.repository.intermediate;

import org.example.testprojectback.model.intermediate.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    @Modifying
    @Query(value = "DELETE FROM groups_subscribers WHERE group_id = :groupId", nativeQuery = true)
    void removeGroupFromUsers(@Param("groupId") Long groupId);
}
