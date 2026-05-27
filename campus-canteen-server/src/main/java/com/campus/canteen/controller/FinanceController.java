package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.entity.Refund;
import com.campus.canteen.entity.Settlement;
import com.campus.canteen.service.FinanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) { this.financeService = financeService; }

    @GetMapping("/settlements")
    public ApiResponse<List<Settlement>> getSettlements() {
        return ApiResponse.ok(financeService.getSettlements());
    }

    @GetMapping("/refunds")
    public ApiResponse<List<Refund>> getRefunds(@RequestParam(required = false) String status) {
        return ApiResponse.ok(financeService.getRefunds(status));
    }

    @PutMapping("/refunds/{id}/approve")
    public ApiResponse<Void> approveRefund(@PathVariable Long id) {
        financeService.approveRefund(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/refunds/{id}/reject")
    public ApiResponse<Void> rejectRefund(@PathVariable Long id) {
        financeService.rejectRefund(id);
        return ApiResponse.ok(null);
    }
}
