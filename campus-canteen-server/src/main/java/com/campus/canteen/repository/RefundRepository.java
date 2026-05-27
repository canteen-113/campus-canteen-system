package com.campus.canteen.repository;

import com.campus.canteen.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByStatus(Refund.Status status);
    long countByStatus(Refund.Status status);
}
