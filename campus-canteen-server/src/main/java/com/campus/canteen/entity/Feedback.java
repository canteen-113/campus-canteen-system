package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "dish_id")
    private Long dishId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 1000)
    private String images;

    @Column(length = 200)
    private String tags;

    @Column(name = "is_anonymous")
    private Integer isAnonymous;

    @Column(columnDefinition = "TEXT")
    private String reply;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum Status { PENDING, REPLIED, CLOSED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (isAnonymous == null) isAnonymous = 0;
        if (status == null) status = Status.PENDING;
    }
}
