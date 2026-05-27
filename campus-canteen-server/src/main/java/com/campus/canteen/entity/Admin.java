package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "canteen_id")
    private Long canteenId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum Role { CANTEEN_MANAGER, SUPERVISOR, SUPER_ADMIN }
    public enum Status { ACTIVE, DISABLED }

    @PrePersist
    void prePersist() {
        createTime = LocalDateTime.now();
        if (status == null) status = Status.ACTIVE;
    }
}
