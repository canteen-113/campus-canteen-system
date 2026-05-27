package com.campus.canteen.service;

import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Ingredient;
import com.campus.canteen.entity.StockRecord;
import com.campus.canteen.repository.IngredientRepository;
import com.campus.canteen.repository.StockRecordRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final IngredientRepository ingredientRepo;
    private final StockRecordRepository stockRecordRepo;

    /**
     * Paginated ingredient list with optional keyword and category filters.
     */
    public PageResult<Ingredient> list(String keyword, String category, int page, int size) {
        Specification<Ingredient> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Ingredient> pageResult = ingredientRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by("createTime").descending()));
        return PageResult.of(pageResult.getContent(), pageResult.getTotalElements(), page, size);
    }

    /**
     * Paginated stock records for a given ingredient, ordered by time descending.
     */
    public PageResult<StockRecord> getRecords(Long ingredientId, int page, int size) {
        List<StockRecord> all = stockRecordRepo.findByIngredientIdOrderByCreateTimeDesc(ingredientId);
        int start = (page - 1) * size;
        if (start >= all.size()) {
            return PageResult.of(List.of(), all.size(), page, size);
        }
        int end = Math.min(start + size, all.size());
        return PageResult.of(all.subList(start, end), all.size(), page, size);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepo.findAll();
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        if (ingredient.getCurrentStock() == null) ingredient.setCurrentStock(BigDecimal.ZERO);
        if (ingredient.getStatus() == null) ingredient.setStatus(Ingredient.Status.SUFFICIENT);
        return ingredientRepo.save(ingredient);
    }

    /**
     * Inbound: increase stock, recalculate avgPrice, create IN record.
     */
    public void inbound(Long ingredientId, BigDecimal quantity, BigDecimal price,
                        String operator, String remark, String recordDate) {
        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("原料不存在: " + ingredientId));

        BigDecimal oldAvgPrice = ingredient.getAvgPrice() != null ? ingredient.getAvgPrice() : BigDecimal.ZERO;
        BigDecimal oldTotalValue = ingredient.getCurrentStock().multiply(oldAvgPrice);
        BigDecimal newValue = quantity.multiply(price);
        BigDecimal newStock = ingredient.getCurrentStock().add(quantity);
        BigDecimal newAvgPrice = newValue.add(oldTotalValue)
                .divide(newStock, 2, RoundingMode.HALF_UP);

        ingredient.setCurrentStock(newStock);
        ingredient.setAvgPrice(newAvgPrice);
        updateStatus(ingredient);
        ingredientRepo.save(ingredient);

        StockRecord record = StockRecord.builder()
                .ingredientId(ingredientId)
                .type(StockRecord.Type.IN)
                .quantity(quantity)
                .price(price)
                .totalAmount(newValue)
                .operator(operator)
                .remark(remark)
                .build();
        if (recordDate != null && !recordDate.isBlank()) {
            record.setCreateTime(java.time.LocalDateTime.parse(recordDate + "T00:00:00"));
        }
        stockRecordRepo.save(record);
    }

    /**
     * Outbound: decrease stock, create OUT record.
     */
    public void outbound(Long ingredientId, BigDecimal quantity,
                         String operator, String remark, String recordDate) {
        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("原料不存在: " + ingredientId));

        if (ingredient.getCurrentStock().compareTo(quantity) < 0) {
            throw new RuntimeException("库存不足: 当前 " + ingredient.getCurrentStock()
                    + ", 需要 " + quantity);
        }
        ingredient.setCurrentStock(ingredient.getCurrentStock().subtract(quantity));
        updateStatus(ingredient);
        ingredientRepo.save(ingredient);

        StockRecord record = StockRecord.builder()
                .ingredientId(ingredientId)
                .type(StockRecord.Type.OUT)
                .quantity(quantity)
                .operator(operator)
                .remark(remark)
                .build();
        if (recordDate != null && !recordDate.isBlank()) {
            record.setCreateTime(java.time.LocalDateTime.parse(recordDate + "T00:00:00"));
        }
        stockRecordRepo.save(record);
    }

    /**
     * Loss: decrease stock, create LOSS record.
     */
    public void loss(Long ingredientId, BigDecimal quantity,
                     String operator, String remark, String recordDate) {
        Ingredient ingredient = ingredientRepo.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("原料不存在: " + ingredientId));

        if (ingredient.getCurrentStock().compareTo(quantity) < 0) {
            throw new RuntimeException("库存不足: 当前 " + ingredient.getCurrentStock()
                    + ", 需要 " + quantity);
        }
        ingredient.setCurrentStock(ingredient.getCurrentStock().subtract(quantity));
        updateStatus(ingredient);
        ingredientRepo.save(ingredient);

        StockRecord record = StockRecord.builder()
                .ingredientId(ingredientId)
                .type(StockRecord.Type.LOSS)
                .quantity(quantity)
                .operator(operator)
                .remark(remark)
                .build();
        if (recordDate != null && !recordDate.isBlank()) {
            record.setCreateTime(java.time.LocalDateTime.parse(recordDate + "T00:00:00"));
        }
        stockRecordRepo.save(record);
    }

    /**
     * Get all ingredients that need attention: stock <= alertThreshold or status EXPIRING.
     */
    public List<Ingredient> getAlerts() {
        List<Ingredient> all = ingredientRepo.findAll();
        return all.stream()
                .filter(i -> i.getStatus() == Ingredient.Status.EXPIRING
                        || (i.getCurrentStock() != null && i.getAlertThreshold() != null
                            && i.getCurrentStock().compareTo(i.getAlertThreshold()) <= 0))
                .toList();
    }

    /**
     * Auto-update ingredient status based on current stock level vs threshold.
     */
    private void updateStatus(Ingredient ing) {
        BigDecimal stock = ing.getCurrentStock();
        BigDecimal threshold = ing.getAlertThreshold();
        if (stock.compareTo(BigDecimal.ZERO) <= 0) {
            ing.setStatus(Ingredient.Status.OUT);
        } else if (threshold != null && stock.compareTo(threshold) <= 0) {
            ing.setStatus(Ingredient.Status.LOW);
        } else {
            ing.setStatus(Ingredient.Status.SUFFICIENT);
        }
    }
}
