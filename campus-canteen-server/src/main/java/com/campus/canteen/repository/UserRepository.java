package com.campus.canteen.repository;

import com.campus.canteen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserNo(String userNo);
    boolean existsByUserNo(String userNo);
    long countByStatus(User.Status status);
}
