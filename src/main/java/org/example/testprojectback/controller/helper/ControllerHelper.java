package org.example.testprojectback.controller.helper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.InterestRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {

    private final InterestRepository interestRepository;

    public void switchInterests(User user, Set<InterestDto> interests){
        if (interests == null) {
            throw new IllegalArgumentException("Interests cannot be null");
        }

        Set<String> interestNames = interests.stream()
                .map(InterestDto::name)
                .collect(Collectors.toSet());

        Set<Interest> newInterests = new HashSet<>(interestRepository.findAllByNameIn(interestNames));

        if (interestNames.size() != newInterests.size()) {
            throw new IllegalArgumentException("Some interests are not found in the database");
        }

        Set<Interest> currentInterests = user.getInterests();

        Set<Interest> interestsToRemove = new HashSet<>(currentInterests);
        interestsToRemove.removeAll(newInterests);

        Set<Interest> interestsToAdd = new HashSet<>(newInterests);
        interestsToAdd.removeAll(currentInterests);

        currentInterests.removeAll(interestsToRemove);
        currentInterests.addAll(interestsToAdd);
    }

    public void switchInterests(Group group,Set<InterestDto> interests){
        if (interests != null) {
            Set<String> interestNames = interests
                    .stream()
                    .map(InterestDto::name)
                    .collect(Collectors.toSet());

            Set<Interest> newInterests = new HashSet<>(interestRepository.findAllByNameIn(interestNames));

            if (newInterests.size() != interestNames.size()) {
                throw new IllegalArgumentException("Some interests are not found in the database");
            }

            Set<Interest> currentInterests = group.getInterests();
            Set<Interest> interestsToRemove = new HashSet<>(currentInterests);
            interestsToRemove.removeAll(newInterests);

            Set<Interest> interestsToAdd = new HashSet<>(newInterests);
            interestsToAdd.removeAll(currentInterests);

            currentInterests.removeAll(interestsToRemove);
            currentInterests.addAll(interestsToAdd);
        }
    }
}
