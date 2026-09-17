package com.mingle.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 나눠서 내려주는 목록 (채팅 목록 / 피드)
 *
 * 화면은 아래로 스크롤하면서 page를 1씩 늘려 가며 이어 붙인다.
 * hasNext가 false면 더 부르지 않는다.
 *
 * @param <T> 목록 항목 (MatchChatResponse, FeedResponse 등)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> items;
    private int page;          // 1부터
    private int size;          // 한 번에 주는 개수
    private int totalCount;
    private boolean hasNext;

    public static <T> PageResponse<T> of(List<T> items, int page, int size, int totalCount) {

        // 지금까지 내려준 개수가 전체보다 적으면 더 남아 있다
        boolean hasNext = (long) page * size < totalCount;

        return new PageResponse<>(items, page, size, totalCount, hasNext);
    }

    /** 요청한 page(1부터)를 건너뛸 개수로 (잘못된 값은 첫 페이지로) */
    public static int toOffset(int page, int size) {
        return (page < 1) ? 0 : (page - 1) * size;
    }
}
