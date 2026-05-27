package com.campus.canteen.repository;

import com.campus.canteen.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
    List<HealthCheck> findByCheckDate(LocalDate checkDate);
}
