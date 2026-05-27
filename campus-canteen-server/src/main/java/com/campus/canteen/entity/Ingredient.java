package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ingredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String spec;

    @Column(length = 20)
    private String unit;

    @Column(length = 50)
    private String category;

    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "alert_threshold", precision = 10, scale = 2)
    private BigDecimal alertThreshold;

    @Column(name = "avg_price", precision = 10, scale = 2)
    private BigDecimal avgPrice;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum Status { SUFFICIENT, LOW, EXPIRING, OUT }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (currentStock == null) currentStock = BigDecimal.ZERO;
        if (status == null) status = Status.SUFFICIENT;
    }

    @PreUpdate
    void preUpdate() { updateTime = LocalDateTime.now(); }
}
