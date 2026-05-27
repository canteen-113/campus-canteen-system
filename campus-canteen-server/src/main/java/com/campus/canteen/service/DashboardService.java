package com.campus.canteen.service;

import com.campus.canteen.entity.Canteen;
import com.campus.canteen.entity.Ingredient;
import com.campus.canteen.entity.Order;
import com.campus.canteen.repository.CanteenRepository;
import com.campus.canteen.repository.DishRepository;
import com.campus.canteen.repository.EmployeeRepository;
import com.campus.canteen.repository.FeedbackRepository;
import com.campus.canteen.repository.IngredientRepository;
import com.campus.canteen.repository.OrderRepository;
import com.campus.canteen.repository.RefundRepository;
import com.campus.canteen.repository.TransactionRepository;
import com.campus.canteen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepo;
    private final DishRepository dishRepo;
    private final IngredientRepository ingredientRepo;
    private final FeedbackRepository feedbackRepo;
    private final EmployeeRepository employeeRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;
    private final CanteenRepository canteenRepo;
    private final RefundRepository refundRepo;

    /**
     * Dashboard data for a specific canteen.
     * Includes today's revenue, diners, pending orders, stock alerts,
     * recent orders, and hourly sales trend.
     */
    public Map<String, Object> getCanteenDashboard(Long canteenId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        List<Order> allOrders = orderRepo.findAll();

        // Today's orders for this canteen
        List<Order> todayOrders = allOrders.stream()
                .filter(o -> o.getCanteenId() != null && o.getCanteenId().equals(canteenId))
                .filter(o -> !o.getCreateTime().isBefore(todayStart)
                        && o.getCreateTime().isBefore(todayEnd))
                .toList();

        // Today revenue
        BigDecimal todayRevenue = todayOrders.stream()
                .map(o -> o.getActualAmount() != null ? o.getActualAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Today diners
        int todayDiners = todayOrders.size();

        // Pending orders (PENDING or PREPARING)
        long pendingOrders = allOrders.stream()
                .filter(o -> o.getCanteenId() != null && o.getCanteenId().equals(canteenId))
                .filter(o -> o.getStatus() == Order.Status.PENDING
                        || o.getStatus() == Order.Status.PREPARING)
                .count();

        // Stock alerts
        long stockAlerts = ingredientRepo.findAll().stream()
                .filter(i -> i.getStatus() == Ingredient.Status.LOW
                        || i.getStatus() == Ingredient.Status.EXPIRING
                        || i.getStatus() == Ingredient.Status.OUT)
                .count();

        // Recent 10 orders (with user/canteen names)
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        allOrders.stream()
                .filter(o -> o.getCanteenId() != null && o.getCanteenId().equals(canteenId))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(10)
                .forEach(o -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", o.getId());
                    item.put("orderNo", o.getOrderNo());
                    item.put("totalAmount", o.getTotalAmount());
                    item.put("actualAmount", o.getActualAmount());
                    item.put("status", o.getStatus() != null ? o.getStatus().name() : null);
                    item.put("createTime", o.getCreateTime());
                    userRepo.findById(o.getUserId())
                            .ifPresent(u -> item.put("userName", u.getName()));
                    canteenRepo.findById(canteenId)
                            .ifPresent(c -> item.put("canteenName", c.getName()));
                    recentOrders.add(item);
                });

        // Hourly sales trend for today
        Map<Integer, Long> hourCounts = new LinkedHashMap<>();
        for (int h = 0; h < 24; h++) {
            hourCounts.put(h, 0L);
        }
        for (Order o : todayOrders) {
            int hour = o.getCreateTime().getHour();
            hourCounts.merge(hour, 1L, Long::sum);
        }
        List<Map<String, Object>> salesTrend = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : hourCounts.entrySet()) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("hour", entry.getKey());
            point.put("count", entry.getValue());
            salesTrend.add(point);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayRevenue", todayRevenue);
        result.put("todayDiners", todayDiners);
        result.put("pendingOrders", pendingOrders);
        result.put("stockAlerts", stockAlerts);
        result.put("recentOrders", recentOrders);
        result.put("salesTrend", salesTrend);
        return result;
    }

    /**
     * Logistics/oversight dashboard: site-wide stats across all canteens.
     * Includes total meals, active diners, supervision tasks, complaint rate,
     * per-canteen status, and ingredient alerts.
     */
    public Map<String, Object> getLogisticsDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        List<Order> allOrders = orderRepo.findAll();

        // Today's orders across all canteens
        List<Order> todayOrders = allOrders.stream()
                .filter(o -> !o.getCreateTime().isBefore(todayStart)
                        && o.getCreateTime().isBefore(todayEnd))
                .toList();

        // Total meals served today
        int totalMeals = todayOrders.size();

        // Distinct diners today
        long activeDiners = todayOrders.stream()
                .map(Order::getUserId)
                .distinct()
                .count();

        // Low-stock ingredient alerts
        List<Ingredient> lowIngredients = ingredientRepo.findByStatus(Ingredient.Status.LOW);
        List<Map<String, Object>> alerts = new ArrayList<>();
        for (Ingredient ing : lowIngredients) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", ing.getId());
            m.put("name", ing.getName());
            m.put("currentStock", ing.getCurrentStock());
            m.put("alertThreshold", ing.getAlertThreshold());
            m.put("unit", ing.getUnit());
            m.put("status", ing.getStatus().name());
            alerts.add(m);
        }

        // Per-canteen order count and revenue for today
        List<Canteen> canteens = canteenRepo.findAll();
        List<Map<String, Object>> canteenStatus = new ArrayList<>();
        for (Canteen c : canteens) {
            List<Order> canteenTodayOrders = todayOrders.stream()
                    .filter(o -> o.getCanteenId() != null && o.getCanteenId().equals(c.getId()))
                    .toList();
            BigDecimal revenue = canteenTodayOrders.stream()
                    .map(o -> o.getActualAmount() != null ? o.getActualAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> cs = new LinkedHashMap<>();
            cs.put("canteenId", c.getId());
            cs.put("canteenName", c.getName());
            cs.put("orderCount", canteenTodayOrders.size());
            cs.put("revenue", revenue);
            canteenStatus.add(cs);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalMeals", totalMeals);
        result.put("activeDiners", activeDiners);
        result.put("supervisionTasks", 8);
        result.put("complaintRate", new BigDecimal("0.02"));
        result.put("canteenStatus", canteenStatus);
        result.put("alerts", alerts);
        return result;
    }

    public Map<String, Object> getStudentDashboard(Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        var user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            result.put("userName", user.getName());
            result.put("userNo", user.getUserNo());
            result.put("balance", user.getBalance());
            result.put("department", user.getDepartment());
        }
        return result;
    }
}
