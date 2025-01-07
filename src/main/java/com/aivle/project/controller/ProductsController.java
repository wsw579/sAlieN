package com.aivle.project.controller;

import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    // Read page (제품 목록 조회 페이지)
    @GetMapping("/products")
    public String products(Model model) {
        List<ProductsEntity> products = productsService.readProducts();

        // 데이터가 null이면 빈 리스트로 초기화
        if (products == null) {
            products = new ArrayList<>();
        }

        model.addAttribute("products", products);
        return "products/products_read"; // templates/products/products_read.mustache 렌더링
    }

    // Detail page (제품 상세 페이지)
    @GetMapping("/products/detail/{productId}")
    public String productDetail(@PathVariable Long productId, Model model) {
        ProductsEntity products = productsService.searchProduct(productId);
        model.addAttribute("products", products);
        return "products/products_detail"; // templates/products/products_detail.mustache 렌더링
    }
}
