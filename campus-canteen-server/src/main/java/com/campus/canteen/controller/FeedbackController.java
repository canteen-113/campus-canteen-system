package com.campus.canteen.controller;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Feedback;
import com.campus.canteen.service.FeedbackService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) { this.feedbackService = feedbackService; }

    @GetMapping
    public ApiResponse<PageResult<Feedback>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(feedbackService.list(status, page, size));
    }

    @PostMapping
    public ApiResponse<Feedback> create(@RequestBody Map<String, Object> body) {
        Long userId = body.get("userId") != null ? ((Number) body.get("userId")).longValue() : null;
        Long orderId = body.get("orderId") != null ? ((Number) body.get("orderId")).longValue() : null;
        Long dishId = body.get("dishId") != null ? ((Number) body.get("dishId")).longValue() : null;
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : 5;
        String content = (String) body.get("content");
        String tags = (String) body.get("tags");
        Integer isAnonymous = body.get("isAnonymous") != null ? ((Number) body.get("isAnonymous")).intValue() : 0;
        String images = (String) body.get("images");
        return ApiResponse.ok(feedbackService.create(userId, orderId, dishId, rating, content, tags, isAnonymous, images));
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<Void> reply(@PathVariable Long id, @RequestParam String reply) {
        feedbackService.reply(id, reply);
        return ApiResponse.ok(null);
    }
}
