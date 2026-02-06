package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.dish.DishCreateDto;
import com.bbqpos.backend.dto.dish.DishDto;
import com.bbqpos.backend.dto.dish.DishUpdateDto;
import com.bbqpos.backend.service.DishService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    @Autowired
    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishDto>> getAllDishes() {
        List<DishDto> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getDishById(@PathVariable Long id) {
        DishDto dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    @PostMapping
    public ResponseEntity<DishDto> createDish(@Valid @RequestBody DishCreateDto dto) {
        DishDto dish = dishService.createDish(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDto> updateDish(@PathVariable Long id, @Valid @RequestBody DishUpdateDto dto) {
        DishDto dish = dishService.updateDish(id, dto);
        return ResponseEntity.ok(dish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getDishCategories() {
        List<String> categories = dishService.getDishCategories();
        return ResponseEntity.ok(categories);
    }

}
