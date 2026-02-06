package com.bbqpos.backend.repository;

import com.bbqpos.backend.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByCategory(String category);

    List<Dish> findByStatus(Dish.Status status);

}
