package com.aivle.project.controller;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.EmployeeDto;
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
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @GetMapping("/orders/revenue-chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getRevenueChartData() {
        return ResponseEntity.ok(ordersService.getChartRevenueData());
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
    public String saveOrder(@ModelAttribute OrdersDto ordersDto) {
        // OrdersEntity 생성 및 저장
        ordersService.createOrder(ordersDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "orders", "", "True", "Success");

        return "redirect:/orders";
    }



    // Update detail page
    @PostMapping("/orders/detail/{orderId}/update")
    public String ordersUpdate(@PathVariable("orderId") Long orderId, @ModelAttribute OrdersDto ordersDto) {
        ordersService.updateOrder(orderId, ordersDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("update", "orders", "", "True", "Success");

        return "redirect:/orders/detail/" + orderId;
    }

    // Delete detail page
    @PostMapping("/orders/detail/{orderId}/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) {
        ordersService.deleteOrder(orderId);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "orders", "", "True", "Success");

        return ResponseEntity.ok().build();
    }

    // Delete orders in bulk
    @PostMapping("/orders/detail/delete")
    public ResponseEntity<Void> deleteOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("Deleting orders with IDs: {}", ids); // 로그 추가
        ordersService.deleteOrdersByIds(ids);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "orders", "", "True", "Success");

        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }

    @GetMapping("/api/sales-performance")
    public ResponseEntity<List<Map<String, Object>>> getSalesPerformanceWithNames() {
        List<Map<String, Object>> salesPerformance = ordersService.getEmployeeSalesPerformanceWithNames();
        return ResponseEntity.ok(salesPerformance);
    }

    @GetMapping("/api/draft-percentage")
    public ResponseEntity<Map<String, Double>> getDraftPercentage() {
        double percentage = 100.0 - ordersService.calculateDraftPercentage();
        Map<String, Double> response = new HashMap<>();
        response.put("draftPercentage", percentage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/available-years")
    public ResponseEntity<Map<String, Integer>> getAvailableYears() {
        return ResponseEntity.ok(ordersService.getAvailableYears());
    }

    @GetMapping("/api/monthly-revenue-purchase")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenueAndPurchase(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String department,
            @RequestParam int year) {
        Map<String, Object> data = ordersService.getMonthlyRevenueAndPurchase(team, department, year);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/api/ordersData")
    public ResponseEntity<List<Map<String, Object>>> getOrdersGroupedByEmployee(@RequestParam(required = false) String team,
                                                                                @RequestParam(required = false) String department,
                                                                                @RequestParam(required = false) String startDate,
                                                                                @RequestParam(required = false) String endDate) {
        try {
            System.out.println("Received Request: team=" + team + ", department=" + department);

            LocalDate now = LocalDate.now();
            LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : now.withDayOfMonth(1);
            LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : now.withDayOfMonth(now.lengthOfMonth());

            List<Map<String, Object>> result;
            if (team != null) {
                result = ordersService.getTeamOrdersGroupedByEmployee(team, start, end);
            } else if (department != null) {
                result = ordersService.getDepartmentOrdersGroupedByEmployee(department, start, end);
            } else {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            System.out.println("Service returned " + result.size() + " grouped orders.");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
