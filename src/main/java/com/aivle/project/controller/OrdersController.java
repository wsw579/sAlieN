package com.aivle.project.controller;

import com.aivle.project.dto.orders.OrdersRequestDto;
import com.aivle.project.dto.orders.OrdersResponseDto;
import com.aivle.project.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    // 주문 생성 폼 (GET /orders/new)
    @GetMapping("/new")
    public String showCreateForm() {
        return "orders/create";  // templates/orders/create.mustache 렌더링
    }

    // 주문 생성 (POST /orders)
    @PostMapping
    public String createOrder(@ModelAttribute OrdersRequestDto requestDto) {
        ordersService.ordersCreate(requestDto);
        return "redirect:/orders";  // 생성 후 목록 페이지로 리다이렉트
    }

    // 모든 주문 조회 (GET /orders)
    @GetMapping
    public String getAllOrders(Model model) {
        List<OrdersResponseDto> orders = ordersService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders/list";  // templates/orders/list.mustache 렌더링
    }

    // 특정 주문 조회 (GET /orders/{id})
    @GetMapping("/{id}")
    public String getOrderById(@PathVariable String id, Model model) {
        OrdersResponseDto order = ordersService.getOrderById(id);
        model.addAttribute("order", order);
        return "orders/detail";  // templates/orders/detail.mustache 렌더링
    }

    // 주문 수정 폼 (GET /orders/{id}/edit)
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable String id, Model model) {
        OrdersResponseDto order = ordersService.getOrderById(id);
        model.addAttribute("order", order);
        return "orders/edit";  // templates/orders/edit.mustache 렌더링
    }

    // 주문 수정 (POST /orders/{id}/edit)
    @PostMapping("/{id}/edit")
    public String updateOrder(@PathVariable String id, @ModelAttribute OrdersRequestDto requestDto) {
        ordersService.ordersUpdate(id, requestDto);
        return "redirect:/orders";  // 수정 후 목록 페이지로 리다이렉트
    }

    // 주문 삭제 (POST /orders/{id}/delete)
    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable String id) {
        ordersService.ordersDelete(id);
        return "redirect:/orders";  // 삭제 후 목록 페이지로 리다이렉트
    }
}
