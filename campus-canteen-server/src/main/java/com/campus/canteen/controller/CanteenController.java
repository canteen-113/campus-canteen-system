package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.entity.Canteen;
import com.campus.canteen.entity.ServiceWindow;
import com.campus.canteen.repository.CanteenRepository;
import com.campus.canteen.repository.ServiceWindowRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canteens")
public class CanteenController {

    private final CanteenRepository canteenRepo;
    private final ServiceWindowRepository windowRepo;

    public CanteenController(CanteenRepository canteenRepo, ServiceWindowRepository windowRepo) {
        this.canteenRepo = canteenRepo;
        this.windowRepo = windowRepo;
    }

    @GetMapping
    public ApiResponse<List<Canteen>> list() {
        return ApiResponse.ok(canteenRepo.findAll());
    }

    @GetMapping("/{id}/windows")
    public ApiResponse<List<ServiceWindow>> getWindows(@PathVariable Long id) {
        return ApiResponse.ok(windowRepo.findByCanteenId(id));
    }
}
