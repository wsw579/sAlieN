package com.aivle.project.controller;

import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    // Read page (주문 목록 조회 페이지)
    @GetMapping("/orders")
    public String orders(Model model) {
        List<OrdersEntity> orders = ordersService.readOrders();

        // 데이터가 null이면 빈 리스트로 초기화
        if (orders == null) {
            orders = new ArrayList<>();
        }

        model.addAttribute("orders", orders);
        return "orders/orders_read"; // templates/orders/orders_read.mustache 렌더링
    }

    // Detail page (주문 상세 페이지)
    @GetMapping("/orders/detail/{orderId}")
    public String orderDetail(@PathVariable Long orderId, Model model) {
        OrdersEntity orders = ordersService.searchOrder(orderId);
        model.addAttribute("orders", orders);
        return "orders/orders_detail"; // templates/orders/orders_detail.mustache 렌더링
    }
}
