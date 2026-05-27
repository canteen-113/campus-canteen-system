package com.campus.canteen.service;

import com.campus.canteen.dto.DashboardVO;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Feedback;
import com.campus.canteen.repository.DishRepository;
import com.campus.canteen.repository.FeedbackRepository;
import com.campus.canteen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final UserRepository userRepo;
    private final DishRepository dishRepo;

    /**
     * Paginated feedback list, optionally filtered by status.
     */
    public PageResult<Feedback> list(String status, int page, int size) {
        List<Feedback> all;
        if (status != null && !status.isBlank()) {
            all = feedbackRepo.findByStatusOrderByCreateTimeDesc(Feedback.Status.valueOf(status));
        } else {
            all = feedbackRepo.findAllByOrderByCreateTimeDesc();
        }
        int start = (page - 1) * size;
        if (start >= all.size()) {
            return PageResult.of(List.of(), all.size(), page, size);
        }
        int end = Math.min(start + size, all.size());
        return PageResult.of(all.subList(start, end), all.size(), page, size);
    }

    /**
     * Create a new feedback. Auto-sets status to PENDING.
     */
    public Feedback create(Long userId, Long orderId, Long dishId, Integer rating,
                           String content, String tags, Integer isAnonymous, String images) {
        Feedback feedback = Feedback.builder()
                .userId(userId)
                .orderId(orderId)
                .dishId(dishId)
                .rating(rating)
                .content(content)
                .tags(tags)
                .isAnonymous(isAnonymous)
                .images(images)
                .status(Feedback.Status.PENDING)
                .build();
        return feedbackRepo.save(feedback);
    }

    /**
     * Reply to a feedback. Sets reply content, replyTime, and status to REPLIED.
     */
    public void reply(Long feedbackId, String reply) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("反馈不存在: " + feedbackId));
        feedback.setReply(reply);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus(Feedback.Status.REPLIED);
        feedbackRepo.save(feedback);
    }

    /**
     * Get feedback statistics: total count, pending replies, complaints, and good rate.
     */
    public DashboardVO feedbackStats() {
        List<Feedback> all = feedbackRepo.findAll();
        long total = all.size();
        long pendingReplies = all.stream()
                .filter(f -> f.getStatus() == Feedback.Status.PENDING)
                .count();
        long complaints = all.stream()
                .filter(f -> f.getRating() != null && f.getRating() <= 2)
                .count();
        long good = all.stream()
                .filter(f -> f.getRating() != null && f.getRating() >= 4)
                .count();
        double goodRate = total > 0 ? (double) good / total : 0.0;

        return DashboardVO.builder()
                .totalFeedbacks((int) total)
                .pendingReplies((int) pendingReplies)
                .complaints((int) complaints)
                .goodRate(goodRate)
                .build();
    }
}
