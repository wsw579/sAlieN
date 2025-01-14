package com.aivle.project.controller;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.*;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.ProductsRepository;
import com.aivle.project.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final ContractsRepository contractsRepository;
    private final OrdersService ordersService;
    private final ProductsRepository productsRepository;

    // Read page
    @GetMapping("/orders")
    public String orders(Model model) {
        List<OrdersEntity> orders = ordersService.readOrders();

        // 데이터가 null이면 빈 리스트로 초기화
        if (orders == null) {
            orders = new ArrayList<>();
        }

        model.addAttribute("orders", orders);
        return "orders/orders_read";
    }

    // Detail page
    @GetMapping("/orders/detail/{orderId}")
    public String ordersDetail(@PathVariable Long orderId, Model model) {
        OrdersEntity orders = ordersService.searchOrder(orderId);
        // 제품 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsEntity> products = productsRepository.findAll();
        List<ContractsEntity> contracts = contractsRepository.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("products", products);
        model.addAttribute("contracts", contracts);
        return "orders/orders_detail";
    }

    // Create order page (초기값으로 페이지 생성)
    @GetMapping("/orders/detail/create")
    public String ordersCreate(Model model) {

        OrdersEntity orders = new OrdersEntity();

        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsEntity> products = productsRepository.findAll();
        List<ContractsEntity> contracts = contractsRepository.findAll();

        orders.setOrderDate(LocalDate.now());
        orders.setSalesDate(LocalDate.now());
        orders.setOrderAmount(0F);
        orders.setOrderStatus(OrderStatus.draft);

        orders.setProductId(new ProductsEntity());
        orders.setContractId(new ContractsEntity());

        model.addAttribute("orders", orders);
        model.addAttribute("products", products);
        model.addAttribute("contracts", contracts);

        return "orders/orders_detail";
    }


    // Create new order

    @PostMapping("/orders/detail/create")
    public String saveOrder(@ModelAttribute OrdersDto ordersDto) {
        // OrdersEntity 생성 및 저장
        ordersService.createOrder(ordersDto);

        return "redirect:/orders";
    }



    // Update detail page
    @PostMapping("/orders/detail/{orderId}/update")
    public String ordersUpdate(@PathVariable("orderId") Long orderId, @ModelAttribute OrdersDto ordersDto) {
        ordersService.updateOrder(orderId, ordersDto);
        return "redirect:/orders/detail/" + orderId;
    }

    // Delete detail page
    @GetMapping("/orders/detail/{orderId}/delete")
    public String ordersDeleteDetail(@PathVariable("orderId") Long orderId) {
        ordersService.deleteOrder(orderId);
        return "redirect:/orders";
    }

    // Delete orders in bulk
    @PostMapping("/orders/detail/delete")
    public ResponseEntity<Void> deleteOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteOrders Received IDs: " + ids); // 로그 추가
        ordersService.deleteOrdersByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }
}
