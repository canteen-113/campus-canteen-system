package com.campus.canteen.service;

import com.campus.canteen.dto.DishVO;
import com.campus.canteen.dto.PageResult;
import com.campus.canteen.entity.Canteen;
import com.campus.canteen.entity.Category;
import com.campus.canteen.entity.Dish;
import com.campus.canteen.entity.ServiceWindow;
import com.campus.canteen.repository.CanteenRepository;
import com.campus.canteen.repository.CategoryRepository;
import com.campus.canteen.repository.DishRepository;
import com.campus.canteen.repository.ServiceWindowRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final CanteenRepository canteenRepository;
    private final ServiceWindowRepository serviceWindowRepository;

    @Transactional(readOnly = true)
    public PageResult<DishVO> list(String keyword, Long categoryId, String status, int page, int size) {
        Specification<Dish> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                        cb.like(root.get("name"), "%" + keyword + "%"),
                        cb.like(root.get("dishNo"), "%" + keyword + "%")));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), Dish.Status.valueOf(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Dish> dishPage = dishRepository.findAll(
                spec,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"))
        );
        List<DishVO> voList = dishPage.getContent().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, dishPage.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public DishVO getById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        return toVO(dish);
    }

    @Transactional
    public DishVO create(Dish dish) {
        dish.setDishNo(generateDishNo());
        Dish saved = dishRepository.save(dish);
        return toVO(saved);
    }

    @Transactional
    public DishVO update(Long id, Dish dish) {
        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        existing.setName(dish.getName());
        existing.setCategoryId(dish.getCategoryId());
        existing.setCanteenId(dish.getCanteenId());
        existing.setWindowId(dish.getWindowId());
        existing.setPrice(dish.getPrice());
        existing.setImage(dish.getImage());
        existing.setCalories(dish.getCalories());
        existing.setNutritionTags(dish.getNutritionTags());
        existing.setDescription(dish.getDescription());
        existing.setDailyStock(dish.getDailyStock());
        existing.setIsRecommended(dish.getIsRecommended());
        if (dish.getStatus() != null) {
            existing.setStatus(dish.getStatus());
        }

        Dish saved = dishRepository.save(existing);
        return toVO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("菜品不存在");
        }
        dishRepository.deleteById(id);
    }

    @Transactional
    public void toggleStatus(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        dish.setStatus(dish.getStatus() == Dish.Status.ON ? Dish.Status.OFF : Dish.Status.ON);
        dishRepository.save(dish);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<DishVO> getRecommended() {
        List<Dish> dishes = dishRepository.findByIsRecommendedAndStatus(1, Dish.Status.ON);
        return dishes.stream().map(this::toVO).collect(Collectors.toList());
    }

    // ======================== private helpers ========================

    private DishVO toVO(Dish dish) {
        String categoryName = null;
        if (dish.getCategoryId() != null) {
            categoryName = categoryRepository.findById(dish.getCategoryId())
                    .map(Category::getName).orElse(null);
        }

        String canteenName = null;
        if (dish.getCanteenId() != null) {
            canteenName = canteenRepository.findById(dish.getCanteenId())
                    .map(Canteen::getName).orElse(null);
        }

        String windowName = null;
        if (dish.getWindowId() != null) {
            windowName = serviceWindowRepository.findById(dish.getWindowId())
                    .map(ServiceWindow::getName).orElse(null);
        }

        return DishVO.builder()
                .id(dish.getId())
                .dishNo(dish.getDishNo())
                .name(dish.getName())
                .categoryId(dish.getCategoryId())
                .categoryName(categoryName)
                .canteenId(dish.getCanteenId())
                .canteenName(canteenName)
                .windowId(dish.getWindowId())
                .windowName(windowName)
                .price(dish.getPrice())
                .image(dish.getImage())
                .calories(dish.getCalories())
                .nutritionTags(dish.getNutritionTags())
                .description(dish.getDescription())
                .dailyStock(dish.getDailyStock())
                .totalSold(dish.getTotalSold())
                .status(dish.getStatus() != null ? dish.getStatus().name() : null)
                .isRecommended(dish.getIsRecommended())
                .build();
    }

    private String generateDishNo() {
        long count = dishRepository.count();
        return "FD-2026-" + String.format("%03d", count + 1);
    }
}
