package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.mapper.InterestDtoMapper;
import org.example.testprojectback.model.Interest;
import org.example.testprojectback.service.InterestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interests")
public class InterestController {

    private final InterestService interestService;
    private final InterestDtoMapper interestDtoMapper;

    private static final String GET_INTEREST = "/interest";

    @GetMapping
    public List<Interest> getInterests() {
        return interestService.getAllInterests();
    }

    @GetMapping(GET_INTEREST)
    public InterestDto getInterestByName(@RequestParam(name = "interestName") String interestName) {
        return interestService.getInterestByName(interestName)
                .map(interestDtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Interest not found"));
    }
}
