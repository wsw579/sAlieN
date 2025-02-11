package com.aivle.project.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PaginationDto<T> {
    private List<T> contents; // 현재 페이지 데이터
    private int currentPage; // 현재 페이지 번호
    private int totalPages; // 총 페이지 수
    private List<Map<String, Object>> pageNumbers; // 페이지 번호 정보
    private boolean hasPreviousPage; // 이전 페이지 존재 여부
    private boolean hasNextPage; // 다음 페이지 존재 여부
    private int previousPage; // 이전 페이지 번호
    private int nextPage; // 다음 페이지 번호
}