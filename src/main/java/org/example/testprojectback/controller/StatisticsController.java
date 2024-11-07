package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.StatisticsDTO;
import org.example.testprojectback.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping
    public ResponseEntity<StatisticsDTO> getStatistics() {
        StatisticsDTO statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
