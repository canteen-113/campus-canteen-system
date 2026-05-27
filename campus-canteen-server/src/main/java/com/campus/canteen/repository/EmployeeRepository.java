package com.campus.canteen.repository;

import com.campus.canteen.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findByCanteenId(Long canteenId);
    long countByStatus(Employee.Status status);
}
