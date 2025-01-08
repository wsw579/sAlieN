package com.aivle.project.controller;

import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    // Read page
    @GetMapping("/products")
    public String products(Model model) {
        List<ProductsEntity> products = productsService.readProducts();

        // 데이터가 null이면 빈 리스트로 초기화
        if (products == null) {
            products = new ArrayList<>();
        }

        model.addAttribute("products", products);
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
        products.setProductCondition("new");
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
    @GetMapping("/products/detail/{productId}/delete")
    public String productsDeleteDetail(@PathVariable("productId") Long productId) {
        productsService.deleteProduct(productId);
        return "redirect:/products";
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
