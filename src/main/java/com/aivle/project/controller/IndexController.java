package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.service.*;
import com.aivle.project.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
    private final EmployeeService employeeService;
    private final OrdersService ordersService;
    private final LeadsService leadsService;
    private final OpportunitiesService opportunitiesService;
    private final PaginationService paginationService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            String userid = UserContext.getCurrentUserId();
            Role userRole = Role.valueOf(UserContext.getCurrentUserRole());
            Position userPosition = Position.valueOf(employeeService.getPositionByUserId(userid));

            // Role이 ROLE_ADMIN이거나, 특정 Position인 경우 admin 페이지 반환
            if (Role.ROLE_ADMIN.equals(userRole) ||
                    Position.GENERAL_MANAGER.equals(userPosition) ||
                    Position.DEPARTMENT_HEAD.equals(userPosition) ||
                    Position.TEAM_LEADER.equals(userPosition)) {
                return "main/index_manager";
            } else {
                return "main/index_user";
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // 역할이 없는 경우, 또는 잘못된 값일 경우 처리
            return "error/unauthorized"; // 에러페이지
        }
    }

    @GetMapping("/rate_plan")
    public String ratePlan(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        return "main/rate_plan";
    }

    //order 관련 api
    @GetMapping("/api/sales-performance")
    public ResponseEntity<List<Map<String, Object>>> getSalesPerformanceWithNames(@RequestParam int year,
                                                                                  @RequestParam int month) {
        String userId = UserContext.getCurrentUserId();
        Role userRole = Role.valueOf(UserContext.getCurrentUserRole());
        Position userPosition = Position.valueOf(employeeService.getPositionByUserId(userId));

        System.out.println("현재 사용자 ID: " + userId);
        System.out.println("현재 사용자 포지션: " + userPosition);

        List<Map<String, Object>> salesPerformance;

        if (Role.ROLE_ADMIN.equals(userRole) || Position.GENERAL_MANAGER.equals(userPosition) || Position.DEPARTMENT_HEAD.equals(userPosition)) {
            salesPerformance = ordersService.getDepartmentSalesPerformance(year, month);
        } else if (Position.TEAM_LEADER.equals(userPosition)) {
            salesPerformance = ordersService.getTeamSalesPerformance(year, month);
        } else {
            salesPerformance = ordersService.getEmployeeSalesPerformanceWithNames(year, month);
        }

        System.out.println("반환 데이터: " + salesPerformance.size() + " 개");

        return ResponseEntity.ok(salesPerformance);
    }

    @GetMapping("/api/draft-percentage")
    public ResponseEntity<Map<String, Double>> getDraftPercentage(@RequestParam int year,
                                                                  @RequestParam int month) {
        double percentage = 100.0 - ordersService.calculateDraftPercentage(year, month);
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
    public ResponseEntity<List<Map<String, Object>>> getOrdersGroupedByEmployee(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        if (team == null && department == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        try {
            LocalDate now = LocalDate.now();
            LocalDate start = Optional.ofNullable(startDate)
                    .map(LocalDate::parse)
                    .orElse(now.withDayOfMonth(1));
            LocalDate end = Optional.ofNullable(endDate)
                    .map(LocalDate::parse)
                    .orElse(now.withDayOfMonth(now.lengthOfMonth()));


            List<Map<String, Object>> result = getOrders(team, department, start, end);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    private List<Map<String, Object>> getOrders(String team, String department, LocalDate start, LocalDate end) {
        return (team != null)
                ? ordersService.getTeamOrdersGroupedByEmployee(team, start, end)
                : ordersService.getDepartmentOrdersGroupedByEmployee(department, start, end);
    }

    // 오늘 추가된 Leads 수 반환
    @GetMapping("/api/leads/today")
    public ResponseEntity<Map<String, Object>> getTodayLeads() {
        long count = leadsService.getTodayLeadsForTeam();
        Map<String, Object> response = new HashMap<>();
        response.put("todayLeads", count);
        return ResponseEntity.ok(response);
    }

    // 특정 상태의 Leads 수 반환
    @GetMapping("/api/leads/status")
    public ResponseEntity<Map<String, Object>> countLeadsByStatus(@RequestParam String leadStatus) {
        long count = leadsService.countLeadsByStatusAndTeam(leadStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("leadStatus", leadStatus);
        response.put("leadCount", count);
        return ResponseEntity.ok(response);
    }

    // 오늘 마감인 Leads 수 반환
    @GetMapping("/api/leads/target-close-today")
    public ResponseEntity<Map<String, Object>> countLeadsWithTargetCloseDateToday() {
        long count = leadsService.countLeadsWithTargetCloseDateTodayForTeam();
        Map<String, Object> response = new HashMap<>();
        response.put("targetCloseDate", "Today");
        response.put("leadCount", count);
        return ResponseEntity.ok(response);
    }

    // 기회카드
    @GetMapping("/api/opportunities/card-value")
    public ResponseEntity<Map<String, Object>> countStatusCardValue() {
        Map<String, Long> statusCounts = opportunitiesService.getOpportunitiesStatusCountsUser();
        Map<String, Object> response = new HashMap<>();
        response.put("statusCounts", statusCounts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/opportunities/card-value-manager")
    public ResponseEntity<Map<String, Object>> countStatusCardValueManager() {
        Map<String, Long> statusCounts = opportunitiesService.getOpportunitiesStatusCountsManager();
        Map<String, Object> response = new HashMap<>();
        response.put("statusCounts", statusCounts);
        return ResponseEntity.ok(response);
    }

    // 진행중 기회 목록 API 추가
    @GetMapping("/api/opportunities/ongoing")
    public String getOngoingOpportunities(@RequestParam(defaultValue = "0") int page, Model model) {

        String userId = UserContext.getCurrentUserId();
        Role userRole = Role.valueOf(UserContext.getCurrentUserRole());
        Position userPosition = Position.valueOf(employeeService.getPositionByUserId(userId));

        PaginationDto<OpportunitiesEntity> paginationDto;

        if (Role.ROLE_ADMIN.equals(userRole) || Position.GENERAL_MANAGER.equals(userPosition) || Position.DEPARTMENT_HEAD.equals(userPosition) || Position.TEAM_LEADER.equals(userPosition)) {
            Page<OpportunitiesEntity> ongoingOpportunities = opportunitiesService.getOngoingOpportunitiesManager(page);
            paginationDto = paginationService.createPaginationData(ongoingOpportunities, page, 5);
        } else {
            Page<OpportunitiesEntity> ongoingOpportunities = opportunitiesService.getOngoingOpportunities(page);
            paginationDto = paginationService.createPaginationData(ongoingOpportunities, page, 5);
        }

        model.addAttribute("pagination", paginationDto);
        return "opportunities/ongoing-opportunities";  // 기존 Mustache 템플릿을 그대로 반환
    }


    @GetMapping("/api/salesData")
    public ResponseEntity<?> getSalesData(
            @RequestParam(required = false) String teamId,
            @RequestParam(required = false) String departmentId
    ) {
        if (teamId == null && departmentId == null) {
            return ResponseEntity.badRequest().body("팀 ID 또는 부서 ID가 필요합니다.");
        }

        Map<String, Object> salesData;
        try {
            salesData = opportunitiesService.getSalesData(teamId, departmentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok(salesData);
    }

    @GetMapping("/api/getLoggedInUser")
    @ResponseBody
    public ResponseEntity<EmployeeDto.Get> getLoggedInUser() {
        try {
            EmployeeDto.Get loggedInUser = employeeService.getLoggedInUser();
            return ResponseEntity.ok(loggedInUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
