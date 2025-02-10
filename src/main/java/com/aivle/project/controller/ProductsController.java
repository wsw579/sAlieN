package com.aivle.project.controller;

import com.aivle.project.dto.PaginationDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.ProductCondition;
import com.aivle.project.service.CrudLogsService;
import com.aivle.project.service.PaginationService;
import com.aivle.project.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductsController {

    private static final Logger logger = LoggerFactory.getLogger(ContractsController.class);
    private static final int DISPLAY_RANGE = 5;

    private final ProductsService productsService;
    private final PaginationService paginationService;

    private final CrudLogsService crudLogsService;

    // Read page
    @GetMapping("/products")
    public String products(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "productName");
        String sortDirection = params.getOrDefault("sortDirection", "asc");
        long aiCount = productsService.countAIProducts();

        Page<ProductsEntity> productsPage = productsService.readProducts(page, size, search, sortColumn, sortDirection);

        // 페이지네이션 데이터 생성
        PaginationDto<ProductsEntity> paginationDto = paginationService.createPaginationData(productsPage, page, 5);

        // 상태별 주문 개수 가져오기
        Map<String, Long> conditionCounts = productsService.getProductConditionCounts();

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

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
        model.addAttribute("discontinuedCount", conditionCounts.getOrDefault("discontinued", 0L));

        // AI 모델의 수
        model.addAttribute("AICount",aiCount);

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
    public String productsCreateNew(@ModelAttribute ProductsDto productsDto, RedirectAttributes redirectAttributes) {
        try {
            // 제품 생성
            productsService.createProduct(productsDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("create", "products", "", "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "제품이 성공적으로 생성되었습니다.");

            return "redirect:/products"; // 성공 시 제품 목록 페이지로 이동
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("create", "products", "", "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "제품 생성 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }

    // Update detail page
    @PostMapping("/products/detail/{productId}/update")
    public String productsUpdate(@PathVariable("productId") Long productId, @ModelAttribute ProductsDto productsDto, RedirectAttributes redirectAttributes) {
        try {
            // 제품 수정
            productsService.updateProduct(productId, productsDto);

            // 성공 로그 기록
            crudLogsService.logCrudOperation("update", "products", productId.toString(), "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "제품이 성공적으로 수정되었습니다.");

            return "redirect:/products/detail/" + productId;
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("update", "products", productId.toString(), "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "제품 수정 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }

    // Delete detail page
    @PostMapping("/products/detail/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            // 제품 삭제 실행
            productsService.deleteProduct(productId);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("delete", "products", productId.toString(), "True", "Success");

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 삭제 실패 로그 기록
            crudLogsService.logCrudOperation("delete", "products", productId.toString(), "False", "Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }

    // Delete products in bulk
    @PostMapping("/products/detail/delete")
    public ResponseEntity<Void> deleteProducts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("Deleting products with IDs: {}", ids); // 로그 추가

        try {
            // 주문 삭제 실행
            productsService.deleteProductsByIds(ids);

            // 개별 ID에 대해 성공 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "products", id.toString(), "True", "Success");
            }

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 개별 ID에 대해 실패 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "products", id.toString(), "False", "Error: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }
}
