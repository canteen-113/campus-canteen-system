package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_windows")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceWindow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canteen_id", nullable = false)
    private Long canteenId;

    @Column(name = "window_no", nullable = false, length = 10)
    private String windowNo;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String operator;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum Status { OPEN, CLOSED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (status == null) status = Status.OPEN;
    }
}
