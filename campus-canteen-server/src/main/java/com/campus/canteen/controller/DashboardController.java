package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }

    @GetMapping("/canteen")
    public ApiResponse<Map<String, Object>> canteen(@RequestParam(required = false) Long canteenId) {
        return ApiResponse.ok(dashboardService.getCanteenDashboard(canteenId));
    }

    @GetMapping("/logistics")
    public ApiResponse<Map<String, Object>> logistics() {
        return ApiResponse.ok(dashboardService.getLogisticsDashboard());
    }

    @GetMapping("/student")
    public ApiResponse<Map<String, Object>> student(@RequestParam Long userId) {
        return ApiResponse.ok(dashboardService.getStudentDashboard(userId));
    }
}
