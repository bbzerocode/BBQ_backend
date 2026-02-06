package com.bbqpos.backend.dto.dish;

import com.bbqpos.backend.model.Dish;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DishDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
    private String category;
    private int stock;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DishDto(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.price = dish.getPrice();
        this.image = dish.getImage();
        this.category = dish.getCategory();
        this.stock = dish.getStock();
        this.status = dish.getStatus().name();
        this.createdAt = dish.getCreatedAt();
        this.updatedAt = dish.getUpdatedAt();
    }

}
