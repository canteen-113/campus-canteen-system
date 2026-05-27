package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 20)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "canteen_id")
    private Long canteenId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_period")
    private MealPeriod mealPeriod;

    @Column(name = "reserve_date")
    private LocalDate reserveDate;

    @Column(name = "reserve_time_start")
    private java.sql.Time reserveTimeStart;

    @Column(name = "reserve_time_end")
    private java.sql.Time reserveTimeEnd;

    @Column(name = "pickup_code", length = 10)
    private String pickupCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum MealPeriod { BREAKFAST, LUNCH, DINNER }
    public enum Status { PENDING, PREPARING, READY, PICKED_UP, CANCELLED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (deposit == null) deposit = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (status == null) status = Status.PENDING;
    }

    @PreUpdate
    void preUpdate() { updateTime = LocalDateTime.now(); }
}
