package com.campus.canteen.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        return PageResult.<T>builder()
                .list(list).total(total).page(page).size(size)
                .totalPages((int) Math.ceil((double) total / size))
                .build();
    }
}
