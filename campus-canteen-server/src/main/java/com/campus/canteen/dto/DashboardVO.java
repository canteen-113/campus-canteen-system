package com.campus.canteen.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardVO {
    // 食堂端指标
    private BigDecimal todayRevenue;
    private Integer todayDiners;
    private Integer pendingOrders;
    private Integer stockAlerts;

    // 监管端指标
    private Integer totalMeals;
    private Integer activeDiners;
    private Integer supervisionTasks;
    private BigDecimal complaintRate;

    // 图表数据
    private List<Map<String, Object>> salesTrend;
    private List<Map<String, Object>> canteenStatus;

    // 员工统计
    private Integer totalEmployees;
    private Integer expiringHealthCert;
    private Integer todayAttendance;
    private BigDecimal monthlyWorkHours;

    // 评价统计
    private Integer totalFeedbacks;
    private Integer pendingReplies;
    private Integer complaints;
    private Double goodRate;

    // 财务统计
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpense;
    private BigDecimal pendingRefund;
    private Integer pendingRefundCount;

    // 库存
    private BigDecimal stockValue;
    private BigDecimal todayOutbound;
    private Integer expiringItems;
}
