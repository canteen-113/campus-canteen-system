package com.campus.canteen.repository;

import com.campus.canteen.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {
    List<ApprovalRecord> findByPoIdOrderByCreateTimeAsc(Long poId);
}
