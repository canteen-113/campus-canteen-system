package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_no", nullable = false, unique = true, length = 20)
    private String poNo;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(length = 100)
    private String department;

    @Column(length = 500)
    private String summary;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum Status { PENDING_MANAGER, PENDING_FINANCE, PENDING_DIRECTOR, APPROVED, REJECTED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) status = Status.PENDING_MANAGER;
    }

    @PreUpdate
    void preUpdate() { updateTime = LocalDateTime.now(); }
}
