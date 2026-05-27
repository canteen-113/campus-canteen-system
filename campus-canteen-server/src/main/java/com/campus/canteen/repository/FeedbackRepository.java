package com.campus.canteen.repository;

import com.campus.canteen.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatusOrderByCreateTimeDesc(Feedback.Status status);
    List<Feedback> findAllByOrderByCreateTimeDesc();
    long countByStatus(Feedback.Status status);
}
