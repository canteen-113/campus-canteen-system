package com.campus.canteen.repository;

import com.campus.canteen.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNo(String orderNo);
    Optional<Order> findByPickupCode(String pickupCode);
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Order> findByCanteenId(Long canteenId);
    long countByStatus(Order.Status status);
}
