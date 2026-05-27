package com.campus.canteen.service;

import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.ApprovalRecord;
import com.campus.canteen.entity.PurchaseItem;
import com.campus.canteen.entity.PurchaseOrder;
import com.campus.canteen.repository.ApprovalRecordRepository;
import com.campus.canteen.repository.EmployeeRepository;
import com.campus.canteen.repository.PurchaseItemRepository;
import com.campus.canteen.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseService {

    private static final BigDecimal SMALL_AMOUNT_THRESHOLD = new BigDecimal("5000");

    private final PurchaseOrderRepository poRepo;
    private final PurchaseItemRepository purchaseItemRepo;
    private final ApprovalRecordRepository approvalRecordRepo;
    private final EmployeeRepository employeeRepo;

    /**
     * Paginated purchase order list, optionally filtered by status.
     */
    public PageResult<PurchaseOrder> list(String status, int page, int size) {
        List<PurchaseOrder> all;
        if (status != null && !status.isBlank()) {
            all = poRepo.findByStatusOrderByCreateTimeDesc(PurchaseOrder.Status.valueOf(status));
        } else {
            all = poRepo.findAllByOrderByCreateTimeDesc();
        }
        int start = (page - 1) * size;
        if (start >= all.size()) {
            return PageResult.of(List.of(), all.size(), page, size);
        }
        int end = Math.min(start + size, all.size());
        return PageResult.of(all.subList(start, end), all.size(), page, size);
    }

    /**
     * Get a single purchase order with its items. Callers may also use
     * getItems(Long poId) to retrieve items separately.
     */
    public PurchaseOrder getById(Long id) {
        return poRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("采购单不存在: " + id));
    }

    /**
     * Get items for a purchase order.
     */
    public List<PurchaseItem> getItems(Long poId) {
        return purchaseItemRepo.findByPoId(poId);
    }

    public Map<String, Object> getDetail(Long id) {
        PurchaseOrder po = getById(id);
        List<PurchaseItem> items = getItems(id);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("po", po);
        detail.put("items", items);
        return detail;
    }

    /**
     * Create from Map (used by controller).
     */
    public PurchaseOrder create(Map<String, Object> body) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        PurchaseOrder po = mapper.convertValue(body.get("po"), PurchaseOrder.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("items");
        List<PurchaseItem> items = itemsRaw != null
                ? itemsRaw.stream().map(m -> mapper.convertValue(m, PurchaseItem.class)).toList()
                : List.of();
        return create(po, items);
    }

    /**
     * Create a new purchase order with items. Generates poNo "PR-YYYYMMDD-XXX".
     */
    public PurchaseOrder create(PurchaseOrder po, List<PurchaseItem> items) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PR-" + today + "-";

        List<PurchaseOrder> all = poRepo.findAll();
        long todayCount = all.stream()
                .filter(p -> p.getPoNo() != null && p.getPoNo().startsWith(prefix))
                .count();
        String poNo = prefix + String.format("%03d", todayCount + 1);
        po.setPoNo(poNo);

        PurchaseOrder saved = poRepo.save(po);

        if (items != null && !items.isEmpty()) {
            for (PurchaseItem item : items) {
                item.setPoId(saved.getId());
                purchaseItemRepo.save(item);
            }
        }
        return saved;
    }

    /**
     * Approve a purchase order at the given step. Advances the PO status:
     * PENDING_MANAGER -> PENDING_FINANCE (or APPROVED if amount < 5000)
     * PENDING_FINANCE -> PENDING_DIRECTOR (if amount > 5000) or APPROVED
     * PENDING_DIRECTOR -> APPROVED
     */
    public void approve(Long poId, String stepName, String approverName, String comment) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("采购单不存在: " + poId));

        ApprovalRecord record = ApprovalRecord.builder()
                .poId(poId)
                .stepName(stepName)
                .approverName(approverName)
                .action(ApprovalRecord.Action.APPROVE)
                .comment(comment)
                .build();
        approvalRecordRepo.save(record);

        BigDecimal amount = po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO;

        switch (po.getStatus()) {
            case PENDING_MANAGER:
                if (amount.compareTo(SMALL_AMOUNT_THRESHOLD) < 0) {
                    po.setStatus(PurchaseOrder.Status.APPROVED);
                } else {
                    po.setStatus(PurchaseOrder.Status.PENDING_FINANCE);
                }
                break;
            case PENDING_FINANCE:
                if (amount.compareTo(SMALL_AMOUNT_THRESHOLD) > 0) {
                    po.setStatus(PurchaseOrder.Status.PENDING_DIRECTOR);
                } else {
                    po.setStatus(PurchaseOrder.Status.APPROVED);
                }
                break;
            case PENDING_DIRECTOR:
                po.setStatus(PurchaseOrder.Status.APPROVED);
                break;
            default:
                throw new RuntimeException("当前采购单状态不允许审批: " + po.getStatus());
        }

        poRepo.save(po);
    }

    /**
     * Reject a purchase order at the given step. Sets PO status to REJECTED.
     */
    public void reject(Long poId, String stepName, String approverName, String comment) {
        PurchaseOrder po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("采购单不存在: " + poId));
        po.setStatus(PurchaseOrder.Status.REJECTED);
        poRepo.save(po);

        ApprovalRecord record = ApprovalRecord.builder()
                .poId(poId)
                .stepName(stepName)
                .approverName(approverName)
                .action(ApprovalRecord.Action.REJECT)
                .comment(comment)
                .build();
        approvalRecordRepo.save(record);
    }

    /**
     * Get all approval records for a purchase order, in chronological order.
     */
    public List<ApprovalRecord> getApprovalRecords(Long poId) {
        return approvalRecordRepo.findByPoIdOrderByCreateTimeAsc(poId);
    }
}
