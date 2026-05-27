package com.campus.canteen.repository;

import com.campus.canteen.entity.ServiceWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceWindowRepository extends JpaRepository<ServiceWindow, Long> {
    List<ServiceWindow> findByCanteenId(Long canteenId);
}
