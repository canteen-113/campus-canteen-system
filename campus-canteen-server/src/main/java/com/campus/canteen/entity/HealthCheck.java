package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_checks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HealthCheck {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(name = "is_qualified")
    private Integer isQualified;

    @Column(length = 200)
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (isQualified == null) isQualified = 1;
    }
}
