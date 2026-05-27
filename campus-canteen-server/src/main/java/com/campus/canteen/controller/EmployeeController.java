package com.campus.canteen.controller;

import com.campus.canteen.config.RequireRole;
import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Employee;
import com.campus.canteen.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) { this.employeeService = employeeService; }

    @GetMapping
    public ApiResponse<PageResult<Employee>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(employeeService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Employee> getById(@PathVariable Long id) {
        return ApiResponse.ok(employeeService.getById(id));
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PostMapping
    public ApiResponse<Employee> create(@RequestBody Employee emp) {
        return ApiResponse.ok(employeeService.create(emp));
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PutMapping("/{id}")
    public ApiResponse<Employee> update(@PathVariable Long id, @RequestBody Employee emp) {
        return ApiResponse.ok(employeeService.update(id, emp));
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.ok(employeeService.getStats());
    }
}
