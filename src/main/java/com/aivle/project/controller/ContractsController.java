package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.*;
import com.aivle.project.repository.*;
import com.aivle.project.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class ContractsController {

    private final ContractsService contractsService;
    private final ProductsService productsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final OpportunitiesService opportunitiesService;
    private final ContractsRepository contractsRepository;


    // Read page
    @GetMapping("/contracts")
    public String contracts(
            @RequestParam(defaultValue = "0") int page, // 현재 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size, // 페이지 크기
            @RequestParam(defaultValue = "") String search, // 검색어
            @RequestParam(defaultValue = "startDate") String sortColumn, // 정렬 기준
            @RequestParam(defaultValue = "desc") String sortDirection, // 정렬 방향
            Model model) {
        Page<ContractsEntity> contractsPage = contractsService.readContracts(page, size, search, sortColumn, sortDirection);

        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = contractsService.getContractStatusCounts();

        // 총 페이지 수 및 표시할 페이지 범위 계산
        int totalPages = contractsPage.getTotalPages();
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

        // 각 상태별 카운트를 가져옴
        Long draftCount = statusCounts.getOrDefault("Draft", 0L);
        Long inApprovalProcessCount = statusCounts.getOrDefault("In Approval Process", 0L);
        Long activatedCount = statusCounts.getOrDefault("Activated", 0L);

// 합계를 계산
        Long allCount = draftCount + inApprovalProcessCount + activatedCount;

        // Model에 데이터 추가
        model.addAttribute("contracts", contractsPage.getContent());
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
        model.addAttribute("isStartDateSorted", "startDate".equals(sortColumn)); // 정렬 기준이 orderDate인지
        model.addAttribute("isTerminationDateSorted", "terminationDate".equals(sortColumn)); // 정렬 기준이 orderAmount인지
        model.addAttribute("isAscSorted", "asc".equals(sortDirection)); // 정렬 방향이 asc인지
        model.addAttribute("isDescSorted", "desc".equals(sortDirection)); // 정렬 방향이 desc인지

        // 상태별 개수 추가
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("inApprovalProcessCount", inApprovalProcessCount);
        model.addAttribute("activatedCount", activatedCount);
        model.addAttribute("allCount", allCount); // 합계 추가
        return "contracts/contracts_read";
    }

    @GetMapping("/contracts/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        Map<String, List<Integer>> barData = contractsService.getBarData();
        return ResponseEntity.ok(barData);
    }

    @GetMapping("/contracts/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        // 서비스에서 데이터를 가져옵니다.
        Map<String, List<Integer>> chartData = contractsService.getChartData();
        return ResponseEntity.ok(chartData);
    }

    // Detail page
    @GetMapping("/contracts/detail/{contractId}")
    public String contracts(@PathVariable Long contractId, Model model) {
        ContractsEntity contracts = contractsService.searchContracts(contractId);
        List<OrdersEntity> orders = contractsService.getOrdersByContractId(contractId);

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
      // List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        // 디버깅을 위해 로그 출력
        System.out.println("Contracts: " + contracts);
        orders.forEach(order -> System.out.println("Order: " + order.getOrderId() + ", Date: " + order.getOrderDate()));

        model.addAttribute("contracts", contracts);
        model.addAttribute("products", products);
//        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("opportunities", opportunities);
        model.addAttribute("orders", orders);
        return "contracts/contracts_detail";
    }

//    @PostMapping("/contracts/detail/{contractId}/createorder")
//    public String createOrder(@ModelAttribute OrdersDto ordersDto, @PathVariable Long contractId) {
//        ContractsEntity contract = contractsRepository.findById(contractId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid contract ID"));
//        ordersService.createOrder(ordersDto, contract);
//        return "redirect:/contracts/detail/" + contractId + "#orderSection";
//    }


    @GetMapping("/contracts/validate")
    @ResponseBody
    public ResponseEntity<?> validateContract(@RequestParam Long contractId) {
        boolean exists = contractsRepository.existsById(contractId);

        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid contract ID");
        }

        return ResponseEntity.ok(Map.of("message", "Valid contract ID"));
    }

    // Create model page (초기값으로 페이지 생성)
    @GetMapping("/contracts/detail/create")
    public String contractsCreate(Model model) {

        ContractsEntity contracts = new ContractsEntity();

        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        contracts.setContractStatus("Draft");
        contracts.setStartDate(LocalDate.now());
        contracts.setTerminationDate(LocalDate.now());
        contracts.setContractDetail("");
        contracts.setContractSales(0);
        contracts.setContractAmount(0);
        contracts.setContractClassification("None");

        contracts.setOpportunityId(new OpportunitiesEntity());
        contracts.setAccountId(new AccountEntity());
        contracts.setProductId(new ProductsEntity());
        contracts.setEmployeeId(new EmployeeEntity());

        model.addAttribute("contracts", contracts);
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("opportunities", opportunities);

        return "contracts/contracts_detail";
    }

    @PostMapping("/contracts/detail/create")
    public String contractsCreateNew(@ModelAttribute ContractsDto contractsDto) {

        contractsService.createContracts(contractsDto);

        return "redirect:/contracts";
    }


    // Update detail page
    @PostMapping("/contracts/detail/{contractId}/update")
    public String contractsUpdate(@PathVariable("contractId") Long contractId, @ModelAttribute ContractsDto contractsDto) {
        contractsService.updateContracts(contractId, contractsDto);
        return "redirect:/contracts/detail/" + contractId;
    }

    // Delete detail page
    @GetMapping("/contracts/detail/{contractId}/delete")
    public String opportunitiesDeleteDetail(@PathVariable("contractId") Long contractId) {
        contractsService.deleteContracts(contractId);

        return "redirect:/contracts";
    }

    // Delete read page (list)
    @PostMapping("/contracts/detail/delete")
    public ResponseEntity<Void> deleteContracts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteContracts Received IDs: " + ids); // 로그 추가
        contractsService.deleteContractsByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }




}
