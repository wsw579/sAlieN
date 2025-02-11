package com.aivle.project.controller;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.OrdersDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.*;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.service.*;
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
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
    private static final int DISPLAY_RANGE = 5;

    private final ContractsRepository contractsRepository;
    private final OrdersService ordersService;
    private final ContractsService contractsService;
    private final ProductsService productsService;
    private final EmployeeService employeeService;

    private final PaginationService paginationService;
    private final CrudLogsService crudLogsService;


    // Read page
    @GetMapping("/orders")
    public String orders(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "orderDate");
        String sortDirection = params.getOrDefault("sortDirection", "desc");

        // 서비스에서 페이지 데이터 가져오기
        Page<OrdersEntity> ordersPage = ordersService.readOrders(page, size, search, sortColumn, sortDirection);
        long numberOfElements = ordersPage.getTotalElements();
        System.out.println(numberOfElements); // 테스트 용
        // 페이지네이션 데이터 생성
        PaginationDto<OrdersEntity> paginationDto = paginationService.createPaginationData(ordersPage, page, 5);

        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = ordersService.getOrderStatusCounts();


        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어
        model.addAttribute("sortColumn", sortColumn); // 정렬 기준
        model.addAttribute("sortDirection", sortDirection); // 정렬 방향

        // 상태별 개수 추가
        model.addAttribute("draftCount", statusCounts.getOrDefault("draft", 0L));
        model.addAttribute("completedCount", statusCounts.getOrDefault("completed", 0L));
        model.addAttribute("activatedCount", statusCounts.getOrDefault("activated", 0L));
        model.addAttribute("cancelledCount", statusCounts.getOrDefault("cancelled", 0L));

        return "orders/orders_read"; // Mustache 템플릿 이름
    }

    @GetMapping("/orders/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(ordersService.getBarData());
    }

    @GetMapping("/orders/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(ordersService.getChartData());
    }

    // Detail page
    @GetMapping("/orders/detail/{orderId}")
    public String ordersDetail(@PathVariable Long orderId, Model model) {
        OrdersEntity orders = ordersService.searchOrder(orderId);

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        // 제품 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<ContractsDto> contracts = contractsService.getAllContractIds();

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
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<ContractsDto> contracts = contractsService.getAllContractIds();

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
    public String saveOrder(@ModelAttribute OrdersDto ordersDto, RedirectAttributes redirectAttributes) {
        try {
            // 주문 생성
            ordersService.createOrder(ordersDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("create", "orders", "", "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "주문이 성공적으로 생성되었습니다.");

            return "redirect:/orders"; // 성공 시 주문 목록 페이지로 이동
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("create", "orders", "", "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "주문 생성 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }



    // Update detail page
    @PostMapping("/orders/detail/{orderId}/update")
    public String ordersUpdate(@PathVariable("orderId") Long orderId, @ModelAttribute OrdersDto ordersDto, RedirectAttributes redirectAttributes) {
        try {
            // 주문 수정
            ordersService.updateOrder(orderId, ordersDto);

            // 성공 로그 기록
            crudLogsService.logCrudOperation("update", "orders", orderId.toString(), "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "주문이 성공적으로 수정되었습니다.");

            return "redirect:/orders/detail/" + orderId;
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("update", "orders", orderId.toString(), "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "주문 수정 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }

    // Delete detail page
    @PostMapping("/orders/detail/{orderId}/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            // 주문 삭제 실행
            ordersService.deleteOrder(orderId);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("delete", "orders", orderId.toString(), "True", "Success");

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 삭제 실패 로그 기록
            crudLogsService.logCrudOperation("delete", "orders", orderId.toString(), "False", "Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }

    // Delete orders in bulk
    @PostMapping("/orders/detail/delete")
    public ResponseEntity<Void> deleteOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        try {
            // 주문 삭제 실행
            ordersService.deleteOrdersByIds(ids);

            // 개별 ID에 대해 성공 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "orders", id.toString(), "True", "Success");
            }

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 개별 ID에 대해 실패 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "orders", id.toString(), "False", "Error: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }
}
