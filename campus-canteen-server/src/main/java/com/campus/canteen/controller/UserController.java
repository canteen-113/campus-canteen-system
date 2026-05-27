package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Transaction;
import com.campus.canteen.entity.User;
import com.campus.canteen.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public ApiResponse<PageResult<User>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(userService.list(keyword, department, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @GetMapping("/{id}/transactions")
    public ApiResponse<List<Transaction>> getTransactions(@PathVariable Long id) {
        return ApiResponse.ok(userService.getTransactions(id));
    }

    @PostMapping("/{id}/recharge")
    public ApiResponse<Void> recharge(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return userService.recharge(id, amount);
    }
}
