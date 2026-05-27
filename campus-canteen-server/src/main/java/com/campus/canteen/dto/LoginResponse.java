package com.campus.canteen.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String role;
    private String name;
    private Long userId;
    private Long canteenId;
    private String userNo;
    private String department;
}
