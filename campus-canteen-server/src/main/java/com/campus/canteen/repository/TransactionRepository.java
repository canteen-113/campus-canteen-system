package com.campus.canteen.repository;

import com.campus.canteen.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByCreateTimeDesc(Long userId);
}
