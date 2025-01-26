package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.LeadsDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.*;
import com.aivle.project.service.AccountService;
import com.aivle.project.service.EmployeeService;
import com.aivle.project.service.LeadsService;
import com.aivle.project.service.PaginationService;
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
    private final PaginationService paginationService;

    // Read Page
    @GetMapping("/leads")
    public String leads(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "createdDate");
        String sortDirection = params.getOrDefault("sortDirection", "desc");


        Page<LeadsEntity> leadsPage = leadsService.readLeads(page, size, search, sortColumn, sortDirection);

        // 페이지네이션 데이터 생성
        PaginationDto<LeadsEntity> paginationDto = paginationService.createPaginationData(leadsPage, page, 5);


        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = leadsService.getLeadStatusCounts();
        long allCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어
        model.addAttribute("sortColumn", sortColumn); // 정렬 기준
        model.addAttribute("sortDirection", sortDirection); // 정렬 방향
        // Mustache 렌더링에 필요한 플래그 추가
        model.addAttribute("isCreatedDateSorted", "createdDate".equals(sortColumn)); // 정렬 기준이 orderDate인지
        model.addAttribute("isTargetCloseDateSorted", "targetCloseDate".equals(sortColumn)); // 정렬 기준이 orderAmount인지
        model.addAttribute("isAscSorted", "asc".equals(sortDirection)); // 정렬 방향이 asc인지
        model.addAttribute("isDescSorted", "desc".equals(sortDirection)); // 정렬 방향이 desc인지
        model.addAttribute("allCount", allCount);

        // 상태별 개수 추가
        model.addAttribute("proposalCount", statusCounts.getOrDefault("Proposal", 0L));
        model.addAttribute("reviewCount", statusCounts.getOrDefault("Under Review", 0L));
        model.addAttribute("acceptedCount", statusCounts.getOrDefault("Accepted", 0L));
        return "leads/leads_read";
    }

    @GetMapping("/leads/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(leadsService.getBarData());
    }

    @GetMapping("/leads/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(leadsService.getChartData());
    }

    //Detail page
    @GetMapping("/leads/detail/{leadId}")
    public String leads(@PathVariable Long leadId, Model model){
        LeadsEntity leads = leadsService.searchLeads(leadId);

        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();



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
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();

        leads.setLeadStatus("");
        leads.setLeadSource("");
        leads.setCreatedDate(LocalDate.now());
        leads.setTargetCloseDate(LocalDate.now());
        leads.setCustomerRequirements("");
        leads.setCustomerRepresentitive("");
        leads.setCompanyName("");
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
    @PostMapping("/leads/detail/{leadId}/delete")
    public ResponseEntity<Void> deleteLead(@PathVariable("leadId") Long leadId) {
        leadsService.deleteLeads(leadId);
        return ResponseEntity.ok().build();
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


