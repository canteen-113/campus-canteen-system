package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.entity.HealthCheck;
import com.campus.canteen.entity.SafetySample;
import com.campus.canteen.service.SafetyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/safety")
public class SafetyController {

    private final SafetyService safetyService;

    public SafetyController(SafetyService safetyService) { this.safetyService = safetyService; }

    @GetMapping("/samples")
    public ApiResponse<List<SafetySample>> getSamples(@RequestParam(required = false) Long canteenId) {
        return ApiResponse.ok(safetyService.getSamples(canteenId));
    }

    @PostMapping("/samples")
    public ApiResponse<SafetySample> createSample(@RequestBody SafetySample sample) {
        return ApiResponse.ok(safetyService.createSample(sample));
    }

    @PutMapping("/samples/{id}")
    public ApiResponse<Void> updateSample(@PathVariable Long id,
                                           @RequestParam String inspector,
                                           @RequestParam(required = false) String image) {
        safetyService.recordSample(id, inspector, image);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/samples/{id}")
    public ApiResponse<Void> deleteSample(@PathVariable Long id) {
        safetyService.deleteSample(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/health-checks")
    public ApiResponse<List<HealthCheck>> getHealthChecks(@RequestParam(required = false) String date) {
        java.time.LocalDate d = date != null ? java.time.LocalDate.parse(date) : null;
        return ApiResponse.ok(safetyService.getHealthChecks(d));
    }

    @PostMapping("/health-checks")
    public ApiResponse<HealthCheck> createHealthCheck(@RequestBody HealthCheck check) {
        return ApiResponse.ok(safetyService.createHealthCheck(check));
    }

    @PutMapping("/health-checks/{id}")
    public ApiResponse<HealthCheck> updateHealthCheck(@PathVariable Long id, @RequestBody HealthCheck update) {
        return ApiResponse.ok(safetyService.updateHealthCheck(id, update));
    }
}
