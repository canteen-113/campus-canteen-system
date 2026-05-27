package com.campus.canteen.repository;

import com.campus.canteen.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByCanteenId(Long canteenId);
}
