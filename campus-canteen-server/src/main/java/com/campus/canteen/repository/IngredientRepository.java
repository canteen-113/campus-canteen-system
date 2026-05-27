package com.campus.canteen.repository;

import com.campus.canteen.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, JpaSpecificationExecutor<Ingredient> {
    List<Ingredient> findByStatus(Ingredient.Status status);
    long countByStatus(Ingredient.Status status);
}
