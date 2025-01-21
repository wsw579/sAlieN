package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.LeadsDto;
import com.aivle.project.entity.*;
import com.aivle.project.service.AccountService;
import com.aivle.project.service.EmployeeService;
import com.aivle.project.service.LeadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
//automatically generates a constructor for all final fields in the class
@RequiredArgsConstructor
public class LeadsController {
    // declares a dependency on the LeadsService class
    private final LeadsService leadsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;

    // Read Page
    @GetMapping("/leads")
    public String leads(
            @RequestParam(defaultValue = "0") int page, // 현재 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size, // 페이지 크기
            @RequestParam(defaultValue = "") String search, // 검색어
            @RequestParam(defaultValue = "createdDate") String sortColumn, // 정렬 기준
            @RequestParam(defaultValue = "desc") String sortDirection, // 정렬 방향
            Model model) {

        Page<LeadsEntity> leadsPage = leadsService.readLeads(page, size, search, sortColumn, sortDirection);

        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = leadsService.getLeadStatusCounts();

        // 총 페이지 수 및 표시할 페이지 범위 계산
        int totalPages = leadsPage.getTotalPages();
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

        // Model에 데이터 추가
        model.addAttribute("leads", leadsPage.getContent());
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
        model.addAttribute("proposalCount", statusCounts.getOrDefault("Proposal", 0L));
        model.addAttribute("reviewCount", statusCounts.getOrDefault("Under Review", 0L));
        model.addAttribute("acceptedCount", statusCounts.getOrDefault("Accepted", 0L));
        return "leads/leads_read";
    }

    @GetMapping("/leads/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        Map<String, List<Integer>> barData = leadsService.getBarData();
        return ResponseEntity.ok(barData);
    }

    @GetMapping("/leads/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        // 서비스에서 데이터를 가져옵니다.
        Map<String, List<Integer>> chartData = leadsService.getChartData();
        return ResponseEntity.ok(chartData);
    }

    //Detail page
    @GetMapping("/leads/detail/{leadId}")
    public String leads(@PathVariable Long leadId, Model model){
        LeadsEntity leads = leadsService.searchLeads(leadId);

        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();


        System.out.println("Leads: "+ leads);


        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        return "leads/leads_detail";
    }

    // Create model page
    // new lead를 만드는 페이지
    @GetMapping("/leads/detail/create")
    public String leadsCreate(Model model) {
        // new instance of the LeadsEntity class
        LeadsEntity leads = new LeadsEntity();

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNames();

        leads.setLeadStatus("");
        leads.setLeadSource("");
        leads.setCreatedDate(LocalDate.now());
        leads.setTargetCloseDate(LocalDate.now());
        leads.setCustomerRequirements("");
        leads.setCustomerRepresentitive("");
        leads.setC_tel("");

        //외래키
        leads.setAccountId(new AccountEntity());
        leads.setEmployeeId(new EmployeeEntity());


        // leads_detail.html 에 "leads"가 보일 수 있도록
        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);

        return "leads/leads_detail";
    }

    @PostMapping("/leads/detail/create")
    public String leadsCreateNew(@ModelAttribute LeadsDto leadsDto){
        // createLeads method in the LeadsService -> passing the leadsDto as an argument
        leadsService.createLeads(leadsDto);
        return "redirect:/leads";
    }

    // Update detail page
    @PostMapping("/leads/detail/{leadId}/update")
    public String leadsUpdate(@PathVariable("leadId") Long leadId, @ModelAttribute LeadsDto leadsDto) {
        leadsService.updateLeads(leadId, leadsDto);
        return "redirect:/leads/detail/" + leadId;
    }

    // Delete detail page
    @GetMapping("/leads/detail/{leadId}/delete")
    public String leadsDeleteDetail(@PathVariable("leadId") Long leadId) {
        leadsService.deleteLeads(leadId);

        return "redirect:/leads";
    }

    // Delete read page (list)
    @PostMapping("/leads/detail/delete")
    public ResponseEntity<Void> deleteLeads(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteLeads Received IDs: " + ids); // 로그 추가
        leadsService.deleteLeadsByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }

}


