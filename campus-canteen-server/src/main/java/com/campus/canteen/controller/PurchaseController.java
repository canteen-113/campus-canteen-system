package com.campus.canteen.controller;

import com.campus.canteen.config.RequireRole;
import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.*;
import com.campus.canteen.service.PurchaseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) { this.purchaseService = purchaseService; }

    @GetMapping
    public ApiResponse<PageResult<PurchaseOrder>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(purchaseService.list(status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getById(@PathVariable Long id) {
        return ApiResponse.ok(purchaseService.getDetail(id));
    }

    @RequireRole({"SUPER_ADMIN", "CANTEEN_MANAGER"})
    @PostMapping
    public ApiResponse<PurchaseOrder> create(@RequestBody Map<String, Object> body) {
        // body contains po (PurchaseOrder) and items (List<PurchaseItem>)
        return ApiResponse.ok(purchaseService.create(body));
    }

    @RequireRole({"SUPER_ADMIN", "SUPERVISOR"})
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id,
                                      @RequestParam String stepName,
                                      @RequestParam String approverName,
                                      @RequestParam(required = false) String comment) {
        purchaseService.approve(id, stepName, approverName, comment);
        return ApiResponse.ok(null);
    }

    @RequireRole({"SUPER_ADMIN", "SUPERVISOR"})
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id,
                                     @RequestParam String stepName,
                                     @RequestParam String approverName,
                                     @RequestParam(required = false) String comment) {
        purchaseService.reject(id, stepName, approverName, comment);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{id}/approvals")
    public ApiResponse<List<ApprovalRecord>> getApprovalRecords(@PathVariable Long id) {
        return ApiResponse.ok(purchaseService.getApprovalRecords(id));
    }
}
