package com.campus.canteen.repository;

import com.campus.canteen.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNo(String poNo);
    List<PurchaseOrder> findByStatusOrderByCreateTimeDesc(PurchaseOrder.Status status);
    List<PurchaseOrder> findAllByOrderByCreateTimeDesc();
}
