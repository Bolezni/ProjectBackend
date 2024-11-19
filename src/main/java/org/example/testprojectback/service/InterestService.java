package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.repository.InterestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    @Transactional
    public Optional<Interest> getInterestByName(String name) {
        return interestRepository.findByName(name);
    }

    @Transactional
    public List<Interest> getAllInterests() {
        return interestRepository.findAll();
    }

    @Transactional
    public void update(String interestName, String color) {
        Interest interest = interestRepository
                .findByName(interestName)
                .orElseThrow(() -> new IllegalArgumentException("Interest with name '" + interestName + "' does not exist"));

        if (color != null && !color.isEmpty() && !color.equals(interest.getColor())) {
            interest.setColor(color);
        }
        interestRepository.save(interest);
    }
}
