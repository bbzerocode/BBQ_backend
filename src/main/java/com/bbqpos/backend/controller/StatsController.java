package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.stats.DishSalesStatsDto;
import com.bbqpos.backend.dto.stats.FinanceStatsDto;
import com.bbqpos.backend.dto.stats.SalesStatsDto;
import com.bbqpos.backend.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dish-sales")
    public ResponseEntity<List<DishSalesStatsDto>> getDishSalesStats(@RequestParam(defaultValue = "30") int days) {
        List<DishSalesStatsDto> stats = statsService.getDishSalesStats(days);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesStatsDto> getSalesStats(@RequestParam(defaultValue = "30") int days) {
        SalesStatsDto stats = statsService.getSalesStats(days);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/finance")
    public ResponseEntity<FinanceStatsDto> getFinanceStats() {
        FinanceStatsDto stats = statsService.getFinanceStats();
        return ResponseEntity.ok(stats);
    }

}
