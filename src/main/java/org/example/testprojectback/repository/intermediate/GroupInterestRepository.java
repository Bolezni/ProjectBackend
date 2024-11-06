package org.example.testprojectback.repository.intermediate;

import org.example.testprojectback.model.intermediate.GroupInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupInterestRepository extends JpaRepository<GroupInterest, Long> {
    @Modifying
    @Query("DELETE FROM GroupInterest gi WHERE gi.group.id = :groupId")
    void deleteAllByGroupId(Long groupId);
}
