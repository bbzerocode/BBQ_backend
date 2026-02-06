package com.bbqpos.backend.repository;

import com.bbqpos.backend.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByType(RestaurantTable.Type type);

    List<RestaurantTable> findByStatus(RestaurantTable.Status status);

}
