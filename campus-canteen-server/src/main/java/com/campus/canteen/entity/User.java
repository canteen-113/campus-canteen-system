package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_no", nullable = false, unique = true, length = 20)
    private String userNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 100)
    private String department;

    @Column(length = 20)
    private String phone;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "face_data")
    private Integer faceData;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum Role { STUDENT, TEACHER, STAFF }
    public enum Status { NORMAL, DISABLED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
        if (faceData == null) faceData = 0;
        if (status == null) status = Status.NORMAL;
    }

    @PreUpdate
    void preUpdate() { updateTime = LocalDateTime.now(); }
}
