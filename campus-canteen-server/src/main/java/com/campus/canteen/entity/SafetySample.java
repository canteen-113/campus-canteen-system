package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "safety_samples")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SafetySample {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canteen_id", nullable = false)
    private Long canteenId;

    @Column(name = "dish_name", nullable = false, length = 100)
    private String dishName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_period")
    private MealPeriod mealPeriod;

    @Column(length = 50)
    private String inspector;

    @Column(length = 500)
    private String image;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum MealPeriod { BREAKFAST, LUNCH, DINNER }
    public enum Status { SAMPLED, PENDING }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (status == null) status = Status.PENDING;
    }
}
