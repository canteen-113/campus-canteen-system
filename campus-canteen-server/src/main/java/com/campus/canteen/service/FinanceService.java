package com.campus.canteen.service;

import com.campus.canteen.entity.*;
import com.campus.canteen.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceService {

    private final SettlementRepository settlementRepo;
    private final RefundRepository refundRepo;
    private final OrderRepository orderRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;

    public FinanceService(SettlementRepository settlementRepo, RefundRepository refundRepo,
                          OrderRepository orderRepo, TransactionRepository transactionRepo,
                          UserRepository userRepo) {
        this.settlementRepo = settlementRepo;
        this.refundRepo = refundRepo;
        this.orderRepo = orderRepo;
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
    }

    public List<Settlement> getSettlements() {
        return settlementRepo.findAll();
    }

    public List<Refund> getRefunds(String status) {
        if (status != null && !status.isEmpty()) {
            return refundRepo.findByStatus(Refund.Status.valueOf(status));
        }
        return refundRepo.findAll();
    }

    @Transactional
    public void approveRefund(Long id) {
        Refund refund = refundRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("退款申请不存在"));
        refund.setStatus(Refund.Status.APPROVED);
        refund.setUpdateTime(LocalDateTime.now());
        refundRepo.save(refund);

        // 退款到用户余额
        User user = userRepo.findById(refund.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setBalance(user.getBalance().add(refund.getAmount()));
        userRepo.save(user);

        // 记录退款流水
        transactionRepo.save(Transaction.builder()
                .userId(refund.getUserId())
                .orderId(refund.getOrderId())
                .amount(refund.getAmount())
                .type(Transaction.Type.REFUND)
                .description("退款: " + refund.getReason())
                .build());
    }

    @Transactional
    public void rejectRefund(Long id) {
        Refund refund = refundRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("退款申请不存在"));
        refund.setStatus(Refund.Status.REJECTED);
        refund.setUpdateTime(LocalDateTime.now());
        refundRepo.save(refund);
    }
}
