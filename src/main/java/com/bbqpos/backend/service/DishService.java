package com.bbqpos.backend.service;

import com.bbqpos.backend.dto.dish.DishCreateDto;
import com.bbqpos.backend.dto.dish.DishDto;
import com.bbqpos.backend.dto.dish.DishUpdateDto;
import com.bbqpos.backend.model.Dish;
import com.bbqpos.backend.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;

    @Autowired
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDto> getAllDishes() {
        return dishRepository.findAll().stream()
                .map(DishDto::new)
                .collect(Collectors.toList());
    }

    public DishDto getDishById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        return new DishDto(dish);
    }

    public DishDto createDish(DishCreateDto dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        dish.setImage(dto.getImage());
        dish.setCategory(dto.getCategory());
        dish.setStock(dto.getStock());
        dish.setStatus(Dish.Status.valueOf(dto.getStatus().toUpperCase()));

        Dish savedDish = dishRepository.save(dish);
        return new DishDto(savedDish);
    }

    public DishDto updateDish(Long id, DishUpdateDto dto) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found"));

        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        dish.setImage(dto.getImage());
        dish.setCategory(dto.getCategory());
        dish.setStock(dto.getStock());
        dish.setStatus(Dish.Status.valueOf(dto.getStatus().toUpperCase()));

        Dish updatedDish = dishRepository.save(dish);
        return new DishDto(updatedDish);
    }

    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }

    public List<String> getDishCategories() {
        return dishRepository.findAll().stream()
                .map(Dish::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

}
