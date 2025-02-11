package com.aivle.project.service;

import com.aivle.project.dto.PaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    public <T> PaginationDto<T> createPaginationData(Page<T> pageData, int currentPage, int displayRange) {
        PaginationDto<T> paginationDto = new PaginationDto<>();

        int totalPages = pageData.getTotalPages();
        int startPage = calculateStartPage(currentPage, displayRange, totalPages);
        int endPage = calculateEndPage(startPage, displayRange, totalPages);

        // 페이지 번호 리스트 생성
        List<Map<String, Object>> pageNumbers = generatePageNumbers(startPage, endPage, currentPage);

        // DTO에 데이터 설정
        paginationDto.setContents(pageData.getContent());
        paginationDto.setCurrentPage(currentPage);
        paginationDto.setTotalPages(totalPages);
        paginationDto.setPageNumbers(pageNumbers);
        paginationDto.setHasPreviousPage(currentPage > 0);
        paginationDto.setHasNextPage(currentPage < totalPages - 1);
        paginationDto.setPreviousPage(Math.max(0, currentPage - 1));
        paginationDto.setNextPage(Math.min(totalPages - 1, currentPage + 1));

        return paginationDto;
    }

    private int calculateStartPage(int currentPage, int displayRange, int totalPages) {
        int startPage = Math.max(0, currentPage - displayRange / 2);
        if (startPage + displayRange > totalPages) {
            startPage = Math.max(0, totalPages - displayRange);
        }
        return startPage;
    }

    private int calculateEndPage(int startPage, int displayRange, int totalPages) {
        return Math.min(totalPages, startPage + displayRange);
    }

    private List<Map<String, Object>> generatePageNumbers(int startPage, int endPage, int currentPage) {
        return IntStream.range(startPage, endPage)
                .mapToObj(page -> {
                    Map<String, Object> pageInfo = new HashMap<>();
                    pageInfo.put("page", page);
                    pageInfo.put("displayPage", page + 1);
                    pageInfo.put("isActive", page == currentPage);
                    return pageInfo;
                })
                .toList();
    }
}




