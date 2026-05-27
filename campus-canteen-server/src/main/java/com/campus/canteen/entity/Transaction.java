package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(length = 200)
    private String description;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum Type { CONSUME, RECHARGE, REFUND }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (paymentMethod == null) paymentMethod = "CARD";
    }
}
