package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.DishVO;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Category;
import com.campus.canteen.entity.Dish;
import com.campus.canteen.service.DishService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) { this.dishService = dishService; }

    @GetMapping
    public ApiResponse<PageResult<DishVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(dishService.list(keyword, categoryId, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<DishVO> getById(@PathVariable Long id) {
        return ApiResponse.ok(dishService.getById(id));
    }

    @PostMapping
    public ApiResponse<DishVO> create(@RequestBody Dish dish) {
        return ApiResponse.ok(dishService.create(dish));
    }

    @PutMapping("/{id}")
    public ApiResponse<DishVO> update(@PathVariable Long id, @RequestBody Dish dish) {
        return ApiResponse.ok(dishService.update(id, dish));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        dishService.toggleStatus(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        return ApiResponse.ok(dishService.getCategories());
    }

    @GetMapping("/recommended")
    public ApiResponse<List<DishVO>> getRecommended() {
        return ApiResponse.ok(dishService.getRecommended());
    }
}
