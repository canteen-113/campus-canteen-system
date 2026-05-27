package com.campus.canteen.repository;

import com.campus.canteen.entity.SafetySample;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SafetySampleRepository extends JpaRepository<SafetySample, Long> {
    List<SafetySample> findByCanteenId(Long canteenId);
}
