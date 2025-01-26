package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.*;
import com.aivle.project.repository.*;
import com.aivle.project.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


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
        String sortDirection = params.getOrDefault("sortDirection", "desc");

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
       List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        logger.info("Contracts: {}", contracts);
        orders.forEach(order -> logger.debug("Order: {}, Date: {}", order.getOrderId(), order.getOrderDate()));

        model.addAttribute("contracts", contracts);
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("opportunities", opportunities);
        model.addAttribute("orders", orders);

        model.addAttribute("uploadedFileName", contracts.getFileName());
        model.addAttribute("contractId", contracts.getContractId());

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
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
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

    // 파일 업로드
    @PostMapping("/contracts/detail/{contractId}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            logger.info("Uploading file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
            if (file.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                return ResponseEntity.badRequest().body("파일 크기는 최대 5MB를 초과할 수 없습니다.");
            }

            contractsService.saveFileToContract(contractId, file);
            return ResponseEntity.ok("파일 업로드 성공");
        } catch (Exception e) {
            logger.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/contracts/detail/{contractId}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long contractId) {
        logger.info("Downloading file for contract ID: {}", contractId);

        ContractsEntity contract = contractsRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));

        if (contract.getFileData() == null) {
            logger.warn("File not found for contract ID: {}", contractId);
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }

        logger.info("File found: {}, size: {}", contract.getFileName(), contract.getFileData().length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + contract.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contract.getMimeType())
                .body(contract.getFileData());
    }

    @DeleteMapping("/contracts/detail/{contractId}/file")
    public ResponseEntity<String> deleteFile(@PathVariable Long contractId) {
        logger.info("파일 삭제 요청 - Contract ID: {}", contractId);

        ContractsEntity contract = contractsRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));

        if (contract.getFileData() == null) {
            logger.warn("Contract ID {}에 파일이 없습니다.", contractId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일이 존재하지 않습니다.");
        }

        contract.setFileData(null);
        contract.setFileName(null);
        contract.setMimeType(null);

        contractsRepository.save(contract);

        logger.info("파일이 성공적으로 삭제되었습니다 - Contract ID: {}", contractId);
        return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
    }
}
