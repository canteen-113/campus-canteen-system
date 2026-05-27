package com.campus.canteen.service;

import com.campus.canteen.dto.OrderItemVO;
import com.campus.canteen.dto.OrderRequest;
import com.campus.canteen.dto.OrderVO;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Canteen;
import com.campus.canteen.entity.Dish;
import com.campus.canteen.entity.Order;
import com.campus.canteen.entity.OrderItem;
import com.campus.canteen.entity.ServiceWindow;
import com.campus.canteen.entity.Transaction;
import com.campus.canteen.entity.User;
import com.campus.canteen.repository.CanteenRepository;
import com.campus.canteen.repository.DishRepository;
import com.campus.canteen.repository.OrderItemRepository;
import com.campus.canteen.repository.OrderRepository;
import com.campus.canteen.repository.ServiceWindowRepository;
import com.campus.canteen.repository.TransactionRepository;
import com.campus.canteen.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final CanteenRepository canteenRepository;
    private final ServiceWindowRepository serviceWindowRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public OrderVO create(OrderRequest req) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderRequest.OrderItemReq item : req.getItems()) {
            Dish dish = dishRepository.findById(item.getDishId())
                    .orElseThrow(() -> new RuntimeException("菜品不存在: " + item.getDishId()));
            if (dish.getDailyStock() < item.getQuantity()) {
                throw new RuntimeException("菜品 " + dish.getName() + " 库存不足");
            }
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal actualAmount = totalAmount.add(req.getDeposit()).subtract(req.getDiscount());

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getBalance().compareTo(actualAmount) < 0) {
            throw new RuntimeException("余额不足");
        }

        Order order = Order.builder()
                .orderNo(generateOrderNo())
                .userId(req.getUserId())
                .canteenId(req.getCanteenId())
                .windowId(req.getWindowId())
                .totalAmount(totalAmount)
                .deposit(req.getDeposit())
                .discount(req.getDiscount())
                .actualAmount(actualAmount)
                .mealPeriod(Order.MealPeriod.valueOf(req.getMealPeriod()))
                .reserveDate(req.getReserveDate() != null ? LocalDate.parse(req.getReserveDate()) : null)
                .reserveTimeStart(parseTime(req.getReserveTimeStart()))
                .reserveTimeEnd(parseTime(req.getReserveTimeEnd()))
                .pickupCode(generatePickupCode())
                .status(Order.Status.PENDING)
                .paymentMethod(req.getPaymentMethod())
                .build();

        order = orderRepository.save(order);

        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderRequest.OrderItemReq item : req.getItems()) {
            Dish dish = dishRepository.findById(item.getDishId()).orElseThrow();
            int newStock = dish.getDailyStock() - item.getQuantity();
            dish.setDailyStock(newStock);
            dish.setTotalSold((dish.getTotalSold() != null ? dish.getTotalSold() : 0) + item.getQuantity());
            if (newStock <= 0) {
              dish.setStatus(Dish.Status.OFF);
            }
            dishRepository.save(dish);

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .dishId(item.getDishId())
                    .dishName(item.getDishName() != null ? item.getDishName() : dish.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            savedItems.add(orderItemRepository.save(orderItem));
        }

        user.setBalance(user.getBalance().subtract(actualAmount));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .userId(req.getUserId())
                .orderId(order.getId())
                .amount(actualAmount)
                .type(Transaction.Type.CONSUME)
                .description("订餐消费 - " + order.getOrderNo())
                .paymentMethod(req.getPaymentMethod())
                .build();
        transactionRepository.save(transaction);

        return toVO(order, savedItems);
    }

    @Transactional(readOnly = true)
    public PageResult<OrderVO> list(Long userId, String status, int page, int size) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), Order.Status.valueOf(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orderPage = orderRepository.findAll(
                spec,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"))
        );

        List<OrderVO> voList = orderPage.getContent().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return toVO(order, items);
                })
                .collect(Collectors.toList());

        return PageResult.of(voList, orderPage.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public OrderVO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toVO(order, items);
    }

    @Transactional
    public OrderVO cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new RuntimeException("订单已取消");
        }
        if (order.getStatus() == Order.Status.PICKED_UP) {
            throw new RuntimeException("已取餐的订单无法取消");
        }

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setBalance(user.getBalance().add(order.getActualAmount()));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .amount(order.getActualAmount())
                .type(Transaction.Type.REFUND)
                .description("取消订餐退款 - " + order.getOrderNo())
                .paymentMethod(order.getPaymentMethod())
                .build();
        transactionRepository.save(transaction);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toVO(order, items);
    }

    @Transactional
    public OrderVO verify(String pickupCode) {
        Order order = orderRepository.findByPickupCode(pickupCode)
                .orElseThrow(() -> new RuntimeException("取餐码无效"));

        if (order.getStatus() == Order.Status.PICKED_UP) {
            throw new RuntimeException("该订单已取餐");
        }

        order.setStatus(Order.Status.PICKED_UP);
        orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toVO(order, items);
    }

    @Transactional(readOnly = true)
    public OrderVO getByPickupCode(String code) {
        Order order = orderRepository.findByPickupCode(code)
                .orElseThrow(() -> new RuntimeException("取餐码无效"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toVO(order, items);
    }

    // ======================== private helpers ========================

    private OrderVO toVO(Order order, List<OrderItem> items) {
        String userName = null;
        String userNo = null;
        if (order.getUserId() != null) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            if (user != null) {
                userName = user.getName();
                userNo = user.getUserNo();
            }
        }

        String canteenName = null;
        if (order.getCanteenId() != null) {
            canteenName = canteenRepository.findById(order.getCanteenId())
                    .map(Canteen::getName).orElse(null);
        }

        String windowName = null;
        if (order.getWindowId() != null) {
            windowName = serviceWindowRepository.findById(order.getWindowId())
                    .map(ServiceWindow::getName).orElse(null);
        }

        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            String dishImage = null;
            if (item.getDishId() != null) {
                dishImage = dishRepository.findById(item.getDishId())
                        .map(Dish::getImage).orElse(null);
            }
            return OrderItemVO.builder()
                    .id(item.getId())
                    .dishId(item.getDishId())
                    .dishName(item.getDishName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .dishImage(dishImage)
                    .build();
        }).collect(Collectors.toList());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .userName(userName)
                .userNo(userNo)
                .canteenId(order.getCanteenId())
                .canteenName(canteenName)
                .windowId(order.getWindowId())
                .windowName(windowName)
                .totalAmount(order.getTotalAmount())
                .deposit(order.getDeposit())
                .discount(order.getDiscount())
                .actualAmount(order.getActualAmount())
                .mealPeriod(order.getMealPeriod() != null ? order.getMealPeriod().name() : null)
                .reserveDate(order.getReserveDate() != null ? order.getReserveDate().toString() : null)
                .reserveTimeStart(order.getReserveTimeStart() != null ? order.getReserveTimeStart().toString() : null)
                .reserveTimeEnd(order.getReserveTimeEnd() != null ? order.getReserveTimeEnd().toString() : null)
                .pickupCode(order.getPickupCode())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .paymentMethod(order.getPaymentMethod())
                .createTime(order.getCreateTime() != null ? order.getCreateTime().format(dtf) : null)
                .items(itemVOs)
                .build();
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count();
        return "#" + datePart + String.format("%04d", count + 1);
    }

    private String generatePickupCode() {
        int random = 100 + (int) (Math.random() * 900);
        return "#A" + random;
    }

    private Time parseTime(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        if (timeStr.length() == 5) {
            timeStr = timeStr + ":00";
        }
        return Time.valueOf(timeStr);
    }
}
