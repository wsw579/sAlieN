package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.*;
import com.aivle.project.repository.*;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class ContractsController {

    private static final Logger logger = LoggerFactory.getLogger(ContractsController.class);
    private static final int DISPLAY_RANGE = 5;

    private final ContractsService contractsService;
    private final ProductsService productsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final OpportunitiesService opportunitiesService;
    private final ContractsRepository contractsRepository;
    private final PaginationService paginationService;
    private final CrudLogsService crudLogsService;

    // Read page
    @GetMapping("/contracts")
    public String contracts(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "startDate");
        String sortDirection = params.getOrDefault("sortDirection", "asc");

        // 데이터 조회
        Page<ContractsEntity> contractsPage = contractsService.readContracts(page, size, search, sortColumn, sortDirection);

        // 페이지네이션 데이터 생성
        PaginationDto<ContractsEntity> paginationDto = paginationService.createPaginationData(contractsPage, page, 5);


        // 상태별 계약 수 가져오기
        Map<String, Long> statusCounts = contractsService.getContractStatusCounts();
        long allCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);
        // 데이터 추가
        model.addAttribute("draftCount", statusCounts.getOrDefault("Draft", 0L));
        model.addAttribute("inApprovalProcessCount", statusCounts.getOrDefault("In Approval Process", 0L));
        model.addAttribute("activatedCount", statusCounts.getOrDefault("Activated", 0L));
        model.addAttribute("allCount", allCount);

        model.addAttribute("search", search);
        model.addAttribute("sortColumn", sortColumn);
        model.addAttribute("sortDirection", sortDirection);

        return "contracts/contracts_read";
    }

    @GetMapping("/contracts/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(contractsService.getBarData());
    }

    @GetMapping("/contracts/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(contractsService.getChartData());
    }


    @GetMapping("/contracts/detail/{contractId}")
    public String contractsDetail(@PathVariable Long contractId, Model model) {
        ContractsEntity contracts = contractsService.searchContracts(contractId);
        List<OrdersEntity> orders = contractsService.getOrdersByContractId(contractId);

        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
      // List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        logger.info("Contracts: {}", contracts);
        orders.forEach(order -> logger.debug("Order: {}, Date: {}", order.getOrderId(), order.getOrderDate()));

        model.addAttribute("contracts", contracts);
        model.addAttribute("products", products);
//        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("opportunities", opportunities);
        model.addAttribute("orders", orders);
        return "contracts/contracts_detail";
    }

    @GetMapping("/contracts/validate")
    @ResponseBody
    public ResponseEntity<ValidationResponseDto> validateContract(@RequestParam Long contractId) {
        boolean exists = contractsRepository.existsById(contractId);

        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ValidationResponseDto("Invalid contract ID"));
        }

        return ResponseEntity.ok(new ValidationResponseDto("Valid contract ID"));
    }

    @GetMapping("/contracts/detail/create")
    public String contractsCreate(Model model) {
        ContractsEntity contracts = new ContractsEntity();

        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        contracts.setContractStatus("");
        contracts.setStartDate(LocalDate.now());
        contracts.setTerminationDate(LocalDate.now());
        contracts.setContractDetail("");
        contracts.setContractSales(0);
        contracts.setContractAmount(0);
        contracts.setContractClassification("");

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

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "contracts", "", "True", "Success");

        return "redirect:/contracts";
    }

    @PostMapping("/contracts/detail/{contractId}/update")
    public String contractsUpdate(@PathVariable("contractId") Long contractId, @ModelAttribute ContractsDto contractsDto) {
        contractsService.updateContracts(contractId, contractsDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("update", "contracts", "", "True", "Success");

        return "redirect:/contracts/detail/" + contractId;
    }

    @PostMapping("/contracts/detail/{contractId}/delete")
    public ResponseEntity<Void> deleteContract(@PathVariable("contractId") Long contractId) {
        contractsService.deleteContracts(contractId);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "contracts", "", "True", "Success");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/contracts/detail/delete")
    public ResponseEntity<Void> deleteContracts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("Deleting contracts with IDs: {}", ids);
        contractsService.deleteContractsByIds(ids);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "contracts", "", "True", "Success");

        return ResponseEntity.ok().build();
    }
}
