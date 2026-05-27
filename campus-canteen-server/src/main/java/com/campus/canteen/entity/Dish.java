package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dishes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Dish {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dish_no", nullable = false, unique = true, length = 20)
    private String dishNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "canteen_id")
    private Long canteenId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String image;

    private Integer calories;

    @Column(name = "nutrition_tags", length = 200)
    private String nutritionTags;

    @Column(length = 500)
    private String description;

    @Column(name = "daily_stock")
    private Integer dailyStock;

    @Column(name = "total_sold")
    private Integer totalSold;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "is_recommended")
    private Integer isRecommended;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum Status { ON, OFF }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (dailyStock == null) dailyStock = 0;
        if (totalSold == null) totalSold = 0;
        if (isRecommended == null) isRecommended = 0;
        if (status == null) status = Status.ON;
    }

    @PreUpdate
    void preUpdate() { updateTime = LocalDateTime.now(); }
}
