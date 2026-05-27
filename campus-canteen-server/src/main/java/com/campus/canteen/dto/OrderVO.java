package com.campus.canteen.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private String userNo;
    private Long canteenId;
    private String canteenName;
    private Long windowId;
    private String windowName;
    private BigDecimal totalAmount;
    private BigDecimal deposit;
    private BigDecimal discount;
    private BigDecimal actualAmount;
    private String mealPeriod;
    private String reserveDate;
    private String reserveTimeStart;
    private String reserveTimeEnd;
    private String pickupCode;
    private String status;
    private String paymentMethod;
    private String createTime;
    private List<OrderItemVO> items;
}
