package com.campus.canteen.service;

import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Transaction;
import com.campus.canteen.entity.User;
import com.campus.canteen.repository.TransactionRepository;
import com.campus.canteen.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public PageResult<User> list(String keyword, String department, int page, int size) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String kw = "%" + keyword + "%";
                Predicate nameLike = cb.like(root.get("name"), kw);
                Predicate userNoLike = cb.like(root.get("userNo"), kw);
                predicates.add(cb.or(nameLike, userNoLike));
            }
            if (StringUtils.hasText(department)) {
                predicates.add(cb.equal(root.get("department"), department));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(
                spec,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"))
        );
        return PageResult.of(userPage.getContent(), userPage.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Transactional
    public ApiResponse<Void> recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .type(Transaction.Type.RECHARGE)
                .description("账户充值")
                .paymentMethod("CARD")
                .build();
        transactionRepository.save(transaction);

        return ApiResponse.ok("充值成功", null);
    }
}
