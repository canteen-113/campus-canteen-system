package com.campus.canteen.repository;

import com.campus.canteen.entity.Canteen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CanteenRepository extends JpaRepository<Canteen, Long> {
    List<Canteen> findByStatus(Canteen.Status status);
}
