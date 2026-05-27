package com.campus.canteen.controller;

import com.campus.canteen.config.RequireRole;
import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.*;
import com.campus.canteen.service.StockService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) { this.stockService = stockService; }

    @GetMapping
    public ApiResponse<PageResult<Ingredient>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(stockService.list(keyword, category, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<Ingredient>> getAll() {
        return ApiResponse.ok(stockService.getAllIngredients());
    }

    @GetMapping("/alerts")
    public ApiResponse<List<Ingredient>> getAlerts() {
        return ApiResponse.ok(stockService.getAlerts());
    }

    @GetMapping("/{id}/records")
    public ApiResponse<PageResult<StockRecord>> getRecords(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(stockService.getRecords(id, page, size));
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PostMapping("/inbound")
    public ApiResponse<Void> inbound(@RequestParam Long ingredientId,
                                      @RequestParam BigDecimal quantity,
                                      @RequestParam(required = false) BigDecimal price,
                                      @RequestParam String operator,
                                      @RequestParam(required = false) String remark,
                                      @RequestParam(required = false) String recordDate) {
        stockService.inbound(ingredientId, quantity, price, operator, remark, recordDate);
        return ApiResponse.ok(null);
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PostMapping("/outbound")
    public ApiResponse<Void> outbound(@RequestParam Long ingredientId,
                                       @RequestParam BigDecimal quantity,
                                       @RequestParam String operator,
                                       @RequestParam(required = false) String remark,
                                       @RequestParam(required = false) String recordDate) {
        stockService.outbound(ingredientId, quantity, operator, remark, recordDate);
        return ApiResponse.ok(null);
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PostMapping("/ingredient")
    public ApiResponse<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        return ApiResponse.ok(stockService.createIngredient(ingredient));
    }

    @PostMapping("/loss")
    public ApiResponse<Void> loss(@RequestParam Long ingredientId,
                                   @RequestParam BigDecimal quantity,
                                   @RequestParam String operator,
                                   @RequestParam(required = false) String remark,
                                   @RequestParam(required = false) String recordDate) {
        stockService.loss(ingredientId, quantity, operator, remark, recordDate);
        return ApiResponse.ok(null);
    }
}
