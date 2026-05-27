package com.campus.canteen.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull private Long userId;
    private Long canteenId;
    private Long windowId;
    @NotEmpty private List<OrderItemReq> items;
    @NotNull private String mealPeriod;
    private String reserveDate;
    private String reserveTimeStart;
    private String reserveTimeEnd;
    private BigDecimal deposit = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private String paymentMethod = "CARD";

    @Data
    public static class OrderItemReq {
        @NotNull private Long dishId;
        private String dishName;
        private Integer quantity = 1;
        @NotNull private BigDecimal price;
    }
}
