package com.campus.canteen.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DishVO {
    private Long id;
    private String dishNo;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Long canteenId;
    private String canteenName;
    private Long windowId;
    private String windowName;
    private BigDecimal price;
    private String image;
    private Integer calories;
    private String nutritionTags;
    private String description;
    private Integer dailyStock;
    private Integer totalSold;
    private String status;
    private Integer isRecommended;
}
