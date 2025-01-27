package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OpportunitiesCommentEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.repository.OpportunitiesRepository;
import com.aivle.project.entity.*;
import com.aivle.project.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;


@Controller
@RequiredArgsConstructor
public class OpportunitiesController {
    private final OpportunitiesService opportunitiesService;
    private final ProductsService productsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final LeadsService leadsService;
    private final PaginationService paginationService;
    private final CrudLogsService crudLogsService;

    // Read page
    @GetMapping("/opportunities")
    public String opportunities(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "createdDate");
        String sortDirection = params.getOrDefault("sortDirection", "desc");

        Page<OpportunitiesEntity> opportunitiesPage = opportunitiesService.readOpportunities(page, size, search, sortColumn, sortDirection);
        PaginationDto<OpportunitiesEntity> paginationDto = paginationService.createPaginationData(opportunitiesPage, page, 5);

        Map<String, Long> statusCounts = opportunitiesService.getOpportunitiesStatusCounts();
        long allCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        model.addAttribute("pagination", paginationDto);

        // 데이터 추가
        model.addAttribute("ongoingCount", statusCounts.getOrDefault("Ongoing", 0L));
        model.addAttribute("pendingCount", statusCounts.getOrDefault("Pending", 0L));
        model.addAttribute("closedCount", statusCounts.getOrDefault("Closed", 0L));
        model.addAttribute("overdueCount", statusCounts.getOrDefault("Overdue", 0L));
        model.addAttribute("allCount", allCount);

        model.addAttribute("search", search);
        model.addAttribute("sortColumn", sortColumn);
        model.addAttribute("sortDirection", sortDirection);

        return "opportunities/opportunities_read";
    }

    @GetMapping("/opportunities/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(opportunitiesService.getBarData());
    }

    @GetMapping("/opportunities/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(opportunitiesService.getChartData());
    }

    @GetMapping("/opportunities/detail/{opportunityId}")
    public String opportunitiesDetail(@PathVariable Long opportunityId, Model model) {
        OpportunitiesEntity opportunities = opportunitiesService.searchOpportunities(opportunityId);
        List<HistoryEntity> history = opportunitiesService.getHistoryByOpportunityId(opportunityId);
        List<OpportunitiesCommentEntity> opportunitiesComments = opportunitiesService.getCommentsByOpportunityId(opportunityId);

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
        List<LeadsDto> leads = leadsService.getAllLeadIdsAndCompanyNames();

        // 직원 목록 추가
        List<EmployeeDto.GetId> employees = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();

        model.addAttribute("employees", employees);
        // 디버깅을 위해 로그 출력
        System.out.println("Opportunities: " + opportunities);
        opportunitiesComments.forEach(comment -> System.out.println("Comment: " + comment.getContent() + ", Date: " + comment.getCommentCreatedDate()));

        model.addAttribute("opportunities", opportunities);
        // 히스토리 수정
        model.addAttribute("history", history);
        model.addAttribute("opportunitiesComments", opportunitiesComments);
        model.addAttribute("products", productsService.getAllProductIdsAndNames());
        model.addAttribute("accounts", accountService.getAllAccountIdsAndNames());
        model.addAttribute("employee", employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds());
        model.addAttribute("leads", leadsService.getAllLeadIdsAndCompanyNames());
        return "opportunities/opportunities_detail";
    }

    @PostMapping("/opportunities/detail/createcomment")
    public String createComment(@RequestParam("content") String content, @RequestParam("opportunityId") Long opportunityId) {
        String employeeId = getCurrentUserId();

        opportunitiesService.createComment(content, opportunityId, employeeId);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "opportunities_comment", "", "True", "Success");

        return "redirect:/opportunities/detail/" + opportunityId + "#commentSection";
    }

    @GetMapping("/opportunities/detail/create")
    public String opportunitiesCreate(Model model) {
        OpportunitiesEntity opportunities = new OpportunitiesEntity();

        opportunities.setOpportunityName("");
        opportunities.setRegion("");
        opportunities.setCompanySize(0);
        opportunities.setOpportunityInquiries("");
        opportunities.setCustomerEmployee("");
        opportunities.setQuantity(0);
        opportunities.setExpectedRevenue(0);
        opportunities.setCompanyRevenue(0);
        opportunities.setOpportunityNotes("");
        opportunities.setCreatedDate(LocalDate.now());
        opportunities.setTargetCloseDate(LocalDate.now());
        opportunities.setOpportunityStatus("");
        opportunities.setSuccessRate("");

        //외래키 부분
        opportunities.setLeadId(new LeadsEntity());
        opportunities.setAccountId(new AccountEntity());
        opportunities.setProductId(new ProductsEntity());
        opportunities.setEmployeeId(new EmployeeEntity());

        model.addAttribute("opportunities", opportunities);
        model.addAttribute("products", productsService.getAllProductIdsAndNames());
        model.addAttribute("accounts", accountService.getAllAccountIdsAndNames());
        model.addAttribute("employee", employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds());
        model.addAttribute("leads", leadsService.getAllLeadIdsAndCompanyNames());

        return "opportunities/opportunities_detail";
    }

    @PostMapping("/opportunities/detail/create")
    public String opportunitiesCreateNew(@ModelAttribute OpportunitiesDto opportunitiesDto) {
        opportunitiesService.createOpportunities(opportunitiesDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "opportunities", "", "True", "Success");

        return "redirect:/opportunities";
    }

    @GetMapping("/opportunities/detail/{opportunityId}/history/create")
    public String historyCreate(@PathVariable Long opportunityId, Model model) {
        HistoryEntity history = new HistoryEntity();
        history.setHistoryDate(LocalDate.now());
        history.setHistoryTime(LocalTime.now());

        model.addAttribute("history", history);
        model.addAttribute("opportunityId", opportunityId);

        return "opportunities/opportunities_history_detail";
    }

    @PostMapping("/opportunities/detail/{opportunityId}/history/create")
    public String createHistory(@PathVariable Long opportunityId, @ModelAttribute HistoryDto historyDto) {
        opportunitiesService.createHistory(historyDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "opportunities_history", "", "True", "Success");


        return "redirect:/opportunities/detail/" + opportunityId;
    }

    @PostMapping("/opportunities/detail/{opportunityId}/update")
    public String opportunitiesUpdate(@PathVariable("opportunityId") Long opportunityId, @ModelAttribute OpportunitiesDto opportunitiesDto) {
        opportunitiesService.updateOpportunities(opportunityId, opportunitiesDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("update", "opportunities", "", "True", "Success");

        return "redirect:/opportunities/detail/" + opportunityId;
    }


    @PostMapping("/opportunities/detail/{opportunityId}/delete")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable("opportunityId") Long opportunityId) {
        opportunitiesService.deleteOpportunities(opportunityId);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "opportunities", "", "True", "Success");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/opportunities/detail/delete")
    public ResponseEntity<Void> deleteOpportunities(@RequestBody Map<String, List<Long>> request) {
        opportunitiesService.deleteOpportunitiesByIds(request.get("ids"));

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "opportunities", "", "True", "Success");

        return ResponseEntity.ok().build();
    }

    // 오늘 마감인 Leads 수 반환
    @GetMapping("/api/opportunities/card-value")
    public ResponseEntity<Map<String, Object>> countStatusCardValue() {
        Map<String, Long> statusCounts = opportunitiesService.getOpportunitiesStatusCountsTeam();
        Map<String, Object> response = new HashMap<>();
        response.put("statusCounts", statusCounts);
        return ResponseEntity.ok(response);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
                ? authentication.getName()
                : null;
    }





}



