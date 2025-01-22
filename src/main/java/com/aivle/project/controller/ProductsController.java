package com.aivle.project.controller;

import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.ProductCondition;
import com.aivle.project.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    // Read page
    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "0") int page, // 현재 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size, // 페이지 크기
            @RequestParam(defaultValue = "") String search, // 검색어
            @RequestParam(defaultValue = "productName") String sortColumn, // 정렬 기준
            @RequestParam(defaultValue = "asc") String sortDirection, // 정렬 방향
            Model model) {
        Page<ProductsEntity> productsPage = productsService.readProducts(page, size, search, sortColumn, sortDirection);

        // 상태별 주문 개수 가져오기
        Map<String, Long> conditionCounts = productsService.getProductConditionCounts();

        // 총 페이지 수 및 표시할 페이지 범위 계산
        int totalPages = productsPage.getTotalPages();
        int displayRange = 5; // 표시할 페이지 버튼 수
        int startPage = Math.max(0, page - displayRange / 2); // 시작 페이지
        int endPage = Math.min(totalPages, startPage + displayRange); // 종료 페이지

        // 시작 페이지와 종료 페이지 범위 조정
        if (endPage - startPage < displayRange) {
            startPage = Math.max(0, endPage - displayRange);
        }

        // 페이지 번호 생성
        List<Map<String, Object>> pageNumbers = IntStream.range(startPage, endPage)
                .mapToObj(i -> {
                    Map<String, Object> pageInfo = new HashMap<>();
                    pageInfo.put("page", i); // 페이지 번호 (0부터 시작)
                    pageInfo.put("displayPage", i + 1); // 사용자에게 보여줄 페이지 번호 (1부터 시작)
                    pageInfo.put("isActive", i == page); // 현재 페이지 여부
                    return pageInfo;
                })
                .toList();

        model.addAttribute("products", productsPage.getContent());

        model.addAttribute("currentPage", page); // 현재 페이지
        model.addAttribute("previousPage", page - 1); // 이전 페이지
        model.addAttribute("nextPage", page + 1); // 다음 페이지
        model.addAttribute("totalPages", totalPages); // 총 페이지 수
        model.addAttribute("hasPreviousPage", page > 0); // 이전 페이지 존재 여부
        model.addAttribute("hasNextPage", page < totalPages - 1); // 다음 페이지 존재 여부
        model.addAttribute("pageNumbers", pageNumbers); // 페이지 번호 목록

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어
        model.addAttribute("sortColumn", sortColumn); // 정렬 기준
        model.addAttribute("sortDirection", sortDirection); // 정렬 방향
        // Mustache 렌더링에 필요한 플래그 추가
        model.addAttribute("isProductNameSorted", "productName".equals(sortColumn)); // 정렬 기준이 productName인지
        model.addAttribute("isAscSorted", "asc".equals(sortDirection)); // 정렬 방향이 asc인지
        model.addAttribute("isDescSorted", "desc".equals(sortDirection)); // 정렬 방향이 desc인지

        // 상태별 개수 추가
        model.addAttribute("availableCount", conditionCounts.getOrDefault("available", 0L));
        model.addAttribute("outOfStockCount", conditionCounts.getOrDefault("out_of_stock", 0L));
        return "products/products_read";
    }

    // Detail page
    @GetMapping("/products/detail/{productId}")
    public String productsDetail(@PathVariable Long productId, Model model) {
        ProductsEntity products = productsService.searchProduct(productId);
        model.addAttribute("products", products);
        return "products/products_detail";
    }

    // Create product page (초기값으로 페이지 생성)
    @GetMapping("/products/detail/create")
    public String productsCreate(Model model) {

        ProductsEntity products = new ProductsEntity();
        products.setProductName("");
        products.setFixedPrice(0F);
        products.setDealerPrice(0F);
        products.setCostPrice(0F);
        products.setProductCondition(ProductCondition.available);
        products.setProductDescription("");
        products.setProductFamily("");

        model.addAttribute("products", products);

        return "products/products_detail";
    }

    // Create new product
    @PostMapping("/products/detail/create")
    public String productsCreateNew(@ModelAttribute ProductsDto productsDto) {
        productsService.createProduct(productsDto);
        return "redirect:/products";
    }

    // Update detail page
    @PostMapping("/products/detail/{productId}/update")
    public String productsUpdate(@PathVariable("productId") Long productId, @ModelAttribute ProductsDto productsDto) {
        productsService.updateProduct(productId, productsDto);
        return "redirect:/products/detail/" + productId;
    }

    // Delete detail page
    @PostMapping("/products/detail/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        productsService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    // Delete products in bulk
    @PostMapping("/products/detail/delete")
    public ResponseEntity<Void> deleteProducts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteProducts Received IDs: " + ids); // 로그 추가
        productsService.deleteProductsByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }
}
