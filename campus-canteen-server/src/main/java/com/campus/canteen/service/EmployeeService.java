package com.campus.canteen.service;

import com.campus.canteen.dto.DashboardVO;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Employee;
import com.campus.canteen.repository.EmployeeRepository;
import com.campus.canteen.repository.HealthCheckRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository empRepo;
    private final HealthCheckRepository healthCheckRepo;

    /**
     * Paginated employee list with optional keyword search on name or empNo.
     */
    public PageResult<Employee> list(String keyword, int page, int size) {
        Specification<Employee> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword + "%";
            Predicate nameMatch = cb.like(root.get("name"), pattern);
            Predicate noMatch = cb.like(root.get("empNo"), pattern);
            return cb.or(nameMatch, noMatch);
        };
        Page<Employee> pageResult = empRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by("createTime").descending()));
        return PageResult.of(pageResult.getContent(), pageResult.getTotalElements(), page, size);
    }

    /**
     * Create a new employee. Generates empNo "EMP-YYYY-XXX".
     */
    public Employee create(Employee emp) {
        int year = LocalDate.now().getYear();
        String prefix = "EMP-" + year + "-";

        List<Employee> all = empRepo.findAll();
        long yearCount = all.stream()
                .filter(e -> e.getEmpNo() != null && e.getEmpNo().startsWith(prefix))
                .count();
        emp.setEmpNo(prefix + String.format("%03d", yearCount + 1));

        return empRepo.save(emp);
    }

    /**
     * Update employee fields. empNo is never changed.
     */
    public Employee update(Long id, Employee emp) {
        Employee existing = empRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在: " + id));

        existing.setName(emp.getName());
        existing.setPosition(emp.getPosition());
        existing.setCanteenId(emp.getCanteenId());
        existing.setPhone(emp.getPhone());
        existing.setHealthCertExpiry(emp.getHealthCertExpiry());
        existing.setHireDate(emp.getHireDate());
        existing.setStatus(emp.getStatus());

        return empRepo.save(existing);
    }

    public Employee getById(Long id) {
        return empRepo.findById(id).orElseThrow(() -> new RuntimeException("员工不存在"));
    }

    public void delete(Long id) {
        empRepo.deleteById(id);
    }

    public List<Employee> getAll() {
        return empRepo.findAll();
    }

    public java.util.Map<String, Object> getStats() {
        long total = empRepo.count();
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAhead = today.plusDays(30);
        List<Employee> all = empRepo.findAll();
        long expiringHealthCert = all.stream()
                .filter(e -> e.getHealthCertExpiry() != null)
                .filter(e -> !e.getHealthCertExpiry().isBefore(today))
                .filter(e -> !e.getHealthCertExpiry().isAfter(thirtyDaysAhead))
                .count();
        long todayAttendance = healthCheckRepo.findByCheckDate(today).size();
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("totalEmployees", (int) total);
        stats.put("expiringHealthCert", (int) expiringHealthCert);
        stats.put("todayAttendance", (int) todayAttendance);
        return stats;
    }
}
