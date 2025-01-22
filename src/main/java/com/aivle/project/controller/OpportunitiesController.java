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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class OpportunitiesController {
    private final OpportunitiesService opportunitiesService;
    private final ProductsService productsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final LeadsService leadsService;
    private final OpportunitiesRepository opportunitiesRepository;

    // Read page
    @GetMapping("/opportunities")
    public String opportunities(
            @RequestParam(defaultValue = "0") int page, // 현재 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size, // 페이지 크기
            @RequestParam(defaultValue = "") String search, // 검색어
            @RequestParam(defaultValue = "createdDate") String sortColumn, // 정렬 기준
            @RequestParam(defaultValue = "desc") String sortDirection, // 정렬 방향
            Model model) {
        Page<OpportunitiesEntity> opportunitiesPage = opportunitiesService.readOpportunities(page, size, search, sortColumn, sortDirection);

        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = opportunitiesService.getOpportunitiesStatusCounts();

        // 총 페이지 수 및 표시할 페이지 범위 계산
        int totalPages = opportunitiesPage.getTotalPages();
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

        model.addAttribute("opportunities", opportunitiesPage.getContent());
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
        model.addAttribute("isCreatedDateSorted", "createdDate".equals(sortColumn)); // 정렬 기준이 orderDate인지
        model.addAttribute("isTargetCloseDateSorted", "targetCloseDate".equals(sortColumn)); // 정렬 기준이 orderAmount인지
        model.addAttribute("isAscSorted", "asc".equals(sortDirection)); // 정렬 방향이 asc인지
        model.addAttribute("isDescSorted", "desc".equals(sortDirection)); // 정렬 방향이 desc인지

        // 상태별 개수 추가
        model.addAttribute("ongoingCount", statusCounts.getOrDefault("Ongoing", 0L));
        model.addAttribute("pendingCount", statusCounts.getOrDefault("Pending", 0L));
        model.addAttribute("closedCount", statusCounts.getOrDefault("Closed", 0L));
        model.addAttribute("overdueCount", statusCounts.getOrDefault("Overdue", 0L));
        return "opportunities/opportunities_read";
    }

    @GetMapping("/opportunities/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        Map<String, List<Integer>> barData = opportunitiesService.getBarData();
        return ResponseEntity.ok(barData);
    }

    @GetMapping("/opportunities/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        // 서비스에서 데이터를 가져옵니다.
        Map<String, List<Integer>> chartData = opportunitiesService.getChartData();
        return ResponseEntity.ok(chartData);
    }

    // Detail page
    @GetMapping("/opportunities/detail/{opportunityId}")
    public String opportunities(@PathVariable Long opportunityId, Model model) {
        OpportunitiesEntity opportunities = opportunitiesService.searchOpportunities(opportunityId);
        // 히스토리 부분 수정
        List<HistoryEntity> history = opportunitiesService.getHistoryByOpportunityId(opportunityId);
        List<OpportunitiesCommentEntity> opportunitiesComments = opportunitiesService.getCommentsByOpportunityId(opportunityId);

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<LeadsDto> leads = leadsService.getAllLeadIdsAndCompanyNames();


        // 디버깅을 위해 로그 출력
        System.out.println("Opportunities: " + opportunities);
        opportunitiesComments.forEach(comment -> System.out.println("Comment: " + comment.getContent() + ", Date: " + comment.getCommentCreatedDate()));

        model.addAttribute("opportunities", opportunities);
        // 히스토리 수정
        model.addAttribute("history", history);
        model.addAttribute("opportunitiesComments", opportunitiesComments);
        model.addAttribute("products", products);
       model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("leads", leads);
        return "opportunities/opportunities_detail";
    }


    // History Detail page
    @GetMapping("/opportunities/detail/{opportunityId}/history/{historyId}")
    public String opportunity_history(@PathVariable Long historyId, @PathVariable Long opportunityId, Model model) {
        HistoryEntity opportunity_history = opportunitiesService.searchHistory(historyId);
        model.addAttribute("history", opportunity_history);
        model.addAttribute("opportunityId", opportunityId);
        return "opportunities/opportunities_history_detail";
    }


    // create comment
    @PostMapping("/opportunities/detail/createcomment")
    public String createComment(@RequestParam("content") String content, @RequestParam("opportunityId") Long opportunityId) {
        opportunitiesService.createComment(content, opportunityId, "작성자"); // 작성자 이름을 실제로 설정
        return "redirect:/opportunities/detail/" + opportunityId + "#commentSection";
    }



    // Create model page (초기값으로 페이지 생성)
    @GetMapping("/opportunities/detail/create")
    public String opportunitiesCreate(Model model) {

        OpportunitiesEntity opportunities = new OpportunitiesEntity();

        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();
        List<LeadsDto> leads = leadsService.getAllLeadIdsAndCompanyNames();


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
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("leads", leads);
        return "opportunities/opportunities_detail";
    }

    @PostMapping("/opportunities/detail/create")
    public String opportunitiesCreateNew(@ModelAttribute OpportunitiesDto opportunitiesDto) {

        opportunitiesService.createOpportunities(opportunitiesDto);

        return "redirect:/opportunities";
    }



    // create history 초기 페이지
    @GetMapping("/opportunities/detail/{opportunityId}/history/create")
    public String historyCreate(@PathVariable Long opportunityId, Model model) {

        HistoryEntity history = new HistoryEntity();
        OpportunitiesEntity opportunity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(()->new IllegalArgumentException("error"));

        history.setHistoryTitle("");
        history.setCustomerRepresentative("");
        history.setHistoryDate(LocalDate.now());
        history.setHistoryTime(LocalTime.now());
        history.setMeetingPlace("");
        history.setActionTaken("");
        history.setCompanySize("");
        history.setCustomerRequirements("");
        history.setOpportunity(new OpportunitiesEntity());

        model.addAttribute("history", history);
        model.addAttribute("opportunityId", opportunityId);

        return "opportunities/opportunities_history_detail";
    }

    @PostMapping("/opportunities/detail/{opportunityId}/history/create")
    public String createHistory(@PathVariable Long opportunityId, @ModelAttribute HistoryDto historyDto) {
        // opportunityId로 OpportunitiesEntity 조회
        OpportunitiesEntity opportunity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid opportunity ID"));

        // HistoryDto에 OpportunitiesEntity를 설정
        historyDto.setOpportunityId(opportunity);  // DTO에 직접 OpportunitiesEntity를 설정

        // 서비스에서 데이터를 저장
        opportunitiesService.createHistory(historyDto);

        return "redirect:/opportunities/detail/" + opportunityId;
    }


    // Update history in detail page
    @PostMapping("/opportunities/detail/{opportunityId}/history/{historyId}/update")
    public String historyUpdate(@PathVariable("historyId") Long historyId, @ModelAttribute HistoryDto historyDto) {
        opportunitiesService.updateHistory(historyId, historyDto);
        return "redirect:/opportunities/detail/{opportunityId}/history/" + historyId;
    }

    @GetMapping("/opportunities/detail/{opportunityId}/history/{historyId}/delete")
    public String historyDeleteDetail(@PathVariable("historyId") Long historyId) {
        opportunitiesService.deleteHistory(historyId);

        return "redirect:/opportunities/detail/{opportunityId}";
    }


    // Update detail page
    @PostMapping("/opportunities/detail/{opportunityId}/update")
    public String opportunitiesUpdate(@PathVariable("opportunityId") Long opportunityId, @ModelAttribute OpportunitiesDto opportunitiesDto) {
        opportunitiesService.updateOpportunities(opportunityId, opportunitiesDto);
        return "redirect:/opportunities/detail/" + opportunityId;
    }

    // Delete detail page
    @GetMapping("/opportunities/detail/{opportunityId}/delete")
    public String opportunitiesDeleteDetail(@PathVariable("opportunityId") Long opportunityId) {
        opportunitiesService.deleteOpportunities(opportunityId);

        return "redirect:/opportunities";
    }

    // Delete read page (list)
    @PostMapping("/opportunities/detail/delete")
    public ResponseEntity<Void> deleteOpportunities(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteOpportunities Received IDs: " + ids); // 로그 추가
        opportunitiesService.deleteOpportunitiesByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }


}

