package com.campus.canteen.repository;

import com.campus.canteen.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    Optional<Dish> findByDishNo(String dishNo);
    List<Dish> findByCategoryId(Long categoryId);
    List<Dish> findByCanteenId(Long canteenId);
    List<Dish> findByStatus(Dish.Status status);
    List<Dish> findByIsRecommendedAndStatus(Integer isRecommended, Dish.Status status);
    long countByStatus(Dish.Status status);
}
