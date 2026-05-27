package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.entity.Admin;
import com.campus.canteen.repository.AdminRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final AdminRepository adminRepo;

    public SystemController(AdminRepository adminRepo) { this.adminRepo = adminRepo; }

    @GetMapping("/roles")
    public ApiResponse<List<Admin>> getRoles() {
        return ApiResponse.ok(adminRepo.findAll());
    }
}
