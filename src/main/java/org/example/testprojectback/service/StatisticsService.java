package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.StatisticsDTO;
import org.example.testprojectback.repository.GroupRepository;
import org.example.testprojectback.repository.InterestRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final InterestRepository interestRepository;

    public StatisticsDTO getStatistics() {
        long userCount = userRepository.count();
        long groupCount = groupRepository.count();
        long interestCount = interestRepository.count();

        return new StatisticsDTO(userCount, groupCount, interestCount);
    }
}
