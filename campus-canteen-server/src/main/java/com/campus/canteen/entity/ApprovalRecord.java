package com.campus.canteen.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Column(name = "step_name", length = 50)
    private String stepName;

    @Column(name = "approver_name", length = 50)
    private String approverName;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(length = 500)
    private String comment;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum Action { APPROVE, REJECT }

    @PrePersist
    void prePersist() { createTime = LocalDateTime.now(); }
}
