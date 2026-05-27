package com.campus.canteen.controller;

import com.campus.canteen.dto.*;
import com.campus.canteen.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ApiResponse<OrderVO> create(@Valid @RequestBody OrderRequest req) {
        return ApiResponse.ok(orderService.create(req));
    }

    @GetMapping
    public ApiResponse<PageResult<OrderVO>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(orderService.list(userId, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderVO> getById(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getById(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderVO> cancel(@PathVariable Long id) {
        return ApiResponse.ok(orderService.cancel(id));
    }

    @GetMapping("/pickup-code/{code}")
    public ApiResponse<OrderVO> getByPickupCode(@PathVariable String code) {
        return ApiResponse.ok(orderService.getByPickupCode(code));
    }

    @PostMapping("/verify")
    public ApiResponse<OrderVO> verify(@RequestParam String code) {
        return ApiResponse.ok(orderService.verify(code));
    }
}
