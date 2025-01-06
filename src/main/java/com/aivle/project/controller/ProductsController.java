package com.aivle.project.controller;

import com.aivle.project.dto.products.ProductsRequestDto;
import com.aivle.project.dto.products.ProductsResponseDto;
import com.aivle.project.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    // 제품 생성 폼 (GET /products/new)
    @GetMapping("/new")
    public String showCreateForm() {
        return "products/create";  // templates/products/create.mustache 렌더링
    }

    // 제품 생성 (POST /products)
    @PostMapping
    public String createProduct(@ModelAttribute ProductsRequestDto requestDto) {
        productsService.productsCreate(requestDto);
        return "redirect:/products";  // 생성 후 목록 페이지로 리다이렉트
    }

    // 모든 제품 조회 (GET /products)
    @GetMapping
    public String getAllProducts(Model model) {
        List<ProductsResponseDto> products = productsService.getAllProducts();
        model.addAttribute("products", products);
        return "products/list";  // templates/products/list.mustache 렌더링
    }

    // 특정 제품 조회 (GET /products/{id})
    @GetMapping("/{id}")
    public String getProductById(@PathVariable String id, Model model) {
        ProductsResponseDto product = productsService.getProductById(id);
        model.addAttribute("product", product);
        return "products/detail";  // templates/products/detail.mustache 렌더링
    }

    // 제품 수정 폼 (GET /products/{id}/edit)
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable String id, Model model) {
        ProductsResponseDto product = productsService.getProductById(id);
        model.addAttribute("product", product);
        return "products/edit";  // templates/products/edit.mustache 렌더링
    }

    // 제품 수정 (POST /products/{id}/edit)
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable String id, @ModelAttribute ProductsRequestDto requestDto) {
        productsService.productsUpdate(id, requestDto);
        return "redirect:/products";  // 수정 후 목록 페이지로 리다이렉트
    }

    // 제품 삭제 (POST /products/{id}/delete)
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable String id) {
        productsService.productsDelete(id);
        return "redirect:/products";  // 삭제 후 목록 페이지로 리다이렉트
    }
}
