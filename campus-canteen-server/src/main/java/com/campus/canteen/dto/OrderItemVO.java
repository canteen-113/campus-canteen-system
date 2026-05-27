package com.campus.canteen.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemVO {
    private Long id;
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private BigDecimal price;
    private String dishImage;
}
