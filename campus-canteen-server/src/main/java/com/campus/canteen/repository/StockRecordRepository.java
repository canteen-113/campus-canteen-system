package com.campus.canteen.repository;

import com.campus.canteen.entity.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {
    List<StockRecord> findByIngredientIdOrderByCreateTimeDesc(Long ingredientId);
}
