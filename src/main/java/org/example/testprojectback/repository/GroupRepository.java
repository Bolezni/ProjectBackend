package org.example.testprojectback.repository;

import org.example.testprojectback.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Stream<Group> streamAllByNameIgnoreCase(String name);
    Stream<Group> streamAllBy();

    List<Group> findByInterests(Set<String> interestIds);

    //streamAllByNameStartsWithIgnoreCase
}
