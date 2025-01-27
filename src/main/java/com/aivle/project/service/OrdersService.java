package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.OrdersRepository;
import com.aivle.project.repository.ProductsRepository;
import com.aivle.project.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdersService {

    private final ContractsRepository contractsRepository;
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    private final EmployeeRepository employeeRepository;

    // Create
    public void createOrder(OrdersDto dto) {
        OrdersEntity orderEntity = convertDtoToEntity(dto);
        ordersRepository.save(orderEntity);
    }

    // Read
    @Transactional(readOnly = true)
    public Page<OrdersEntity> readOrders(int page, int size, String search, String sortColumn, String sortDirection) {
        String userid = UserContext.getCurrentUserId();
        String userrole = UserContext.getCurrentUserRole();
        String userdept = employeeRepository.findDeptById(userid);
        String userteam = employeeRepository.findTeamById(userid);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if ("ROLE_ADMIN".equals(userrole)) {
            return findOrdersForAdmin(search, pageable);
        } else if ("ROLE_USER".equals(userrole)) {
            return findOrdersForUser(search, userdept, userteam, pageable);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }


    // Update
    @Transactional
    public void updateOrder(Long orderId, OrdersDto dto) {
        OrdersEntity ordersEntity = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        updateEntityFromDto(ordersEntity, dto);

        ordersRepository.save(ordersEntity);
    }

    // Delete
    public void deleteOrder(Long orderId) {
        ordersRepository.deleteById(orderId);
    }

    // Delete multiple orders by IDs
    public void deleteOrdersByIds(List<Long> ids) {
        if (ids.size() == 1) {
            ordersRepository.deleteById(ids.get(0));  // 단일 ID에 대해 개별 메서드 호출
        } else {
            ordersRepository.deleteAllById(ids);  // 다중 ID에 대해 메서드 호출
        }
    }

    // Search
    public OrdersEntity searchOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    // 테이블 데이터 전달
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderPageData(int page, int size, String search, String sortColumn, String sortDirection) {
        Page<OrdersEntity> ordersPage = readOrders(page, size, search, sortColumn, sortDirection);
        Map<String, Long> statusCounts = getOrderStatusCounts();

        Map<String, Object> data = new HashMap<>();
        data.put("ordersPage", ordersPage);
        data.put("statusCounts", statusCounts);
        data.put("totalCount", statusCounts.values().stream().mapToLong(Long::longValue).sum());

        return data;
    }

    // 상태 수 가져오기
    @Transactional(readOnly = true)
    public Map<String, Long> getOrderStatusCounts() {
        String userid = UserContext.getCurrentUserId();
        String userdept = employeeRepository.findDeptById(userid);
        String userteam = employeeRepository.findTeamById(userid);

        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> results = ordersRepository.countOrdersByStatusForCurrentUser(userid, Dept.valueOf(userdept), Team.valueOf(userteam));

        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
        }

        return statusCounts;
    }

    // Bar 및 Chart Data
    public Map<String, List<Integer>> getBarData() {
        return getYearlyData(true, false);
    }

    public Map<String, List<Integer>> getChartData() {
        return getYearlyData(false, false);
    }

    public Map<String, List<Integer>> getChartRevenueData() {
        return getYearlyData(false, true);
    }

    private Map<String, List<Integer>> getYearlyData(boolean accumulate, boolean revenue) {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        List<Integer> lastYearData = initializeMonthlyData();
        List<Integer> currentYearData = initializeMonthlyData();

        if (revenue){
            revenueMonthlyData(lastYear, lastYearData);
            revenueMonthlyData(currentYear, currentYearData);
        } else{
            populateMonthlyData(lastYear, lastYearData);
            populateMonthlyData(currentYear, currentYearData);
        }

        if (accumulate) {
            accumulateMonthlyData(lastYearData);
            accumulateMonthlyData(currentYearData);
        }

        Map<String, List<Integer>> yearlyData = new HashMap<>();
        yearlyData.put("lastYearData", lastYearData);
        yearlyData.put("currentYearData", currentYearData);
        return yearlyData;
    }

    private List<Integer> initializeMonthlyData() {
        return IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());
    }

    private void populateMonthlyData(int year, List<Integer> monthlyData) {
        ordersRepository.getMonthlyOrders(year)
                .forEach(row -> {
                    int month = ((Number) row[0]).intValue() - 1;
                    int count = ((Number) row[1]).intValue();
                    monthlyData.set(month, count);
                });
    }

    private void revenueMonthlyData(int year, List<Integer> monthlyData) {
        String userid = UserContext.getCurrentUserId();
        String userteam = employeeRepository.findTeamById(userid);
        ordersRepository.getMonthlyRevenue(year, Team.valueOf(userteam))
                .forEach(row -> {
                    int month = ((Number) row[0]).intValue() - 1;
                    int revenue = ((Number) row[1]).intValue();
                    monthlyData.set(month, revenue);
                });
    }

    private void accumulateMonthlyData(List<Integer> monthlyData) {
        for (int i = 1; i < monthlyData.size(); i++) {
            monthlyData.set(i, monthlyData.get(i) + monthlyData.get(i - 1));
        }
    }

    // 헬퍼 메서드
    private OrdersEntity convertDtoToEntity(OrdersDto dto) {
        OrdersEntity ordersEntity = new OrdersEntity();
        updateEntityFromDto(ordersEntity, dto);
        return ordersEntity;
    }

    private void updateEntityFromDto(OrdersEntity entity, OrdersDto dto) {
        // 현재 사용자 정보 가져오기
        String currentUserId = UserContext.getCurrentUserId();
        // 데이터베이스에서 EmployeeEntity 로드
        EmployeeEntity employee = employeeRepository.findByEmployeeId(currentUserId);

        entity.setOrderDate(dto.getOrderDate());
        entity.setSalesDate(dto.getSalesDate());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        entity.setContractId(dto.getContractId());
        entity.setProductId(dto.getProductId());
        entity.setEmployeeId(employee);
        ordersRepository.save(entity);
    }

    private Page<OrdersEntity> findOrdersForAdmin(String search, Pageable pageable) {
        // Admin 전용 로직
        if (search != null && !search.isEmpty()) {
            return ordersRepository.findByOrderIdLikeAdmin("%" + search + "%", pageable);
        }
        return ordersRepository.findAll(pageable);
    }

    private Page<OrdersEntity> findOrdersForUser(String search, String departmentId, String teamId, Pageable pageable) {
        // User 전용 로직
        if (search != null && !search.isEmpty()) {
            return ordersRepository.findByOrderIdLikeUser("%" + search + "%", Dept.valueOf(departmentId), Team.valueOf(teamId), pageable);
        }
        return ordersRepository.findByDepartmentAndTeam(Dept.valueOf(departmentId), Team.valueOf(teamId), pageable);
    }

    // 영업 실적 그래프
    public List<Map<String, Object>> getEmployeeSalesPerformanceWithNames() {
        String userid = UserContext.getCurrentUserId();
        String userteam = employeeRepository.findTeamById(userid);
        List<Object[]> data = ordersRepository.getSalesByEmployeeWithNames(Team.valueOf(userteam));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("employeeId", row[0]);
            map.put("employeeName", row[1]);
            map.put("totalSales", row[2]);
            result.add(map);
        }
        return result;
    }

    // 주문현황 퍼센트 표시
    public double calculateDraftPercentage() {
        long totalSalesThisMonth = ordersRepository.countTotalSalesThisMonth();
        long draftSalesThisMonth = ordersRepository.countDraftSalesThisMonth();
        if (totalSalesThisMonth == 0) {
            return 100.0; // 분모가 0인 경우 비율은 0
        }

        return (double) draftSalesThisMonth / totalSalesThisMonth * 100;
    }

    //관리자 페이지 매출현황
    public Map<String, Integer> getAvailableYears() {
        Object[] rawResult = ordersRepository.findMinAndMaxYears();

        // 첫 번째 배열 추출
        Object[] result = (Object[]) rawResult[0];
        int minYear = ((Number) result[0]).intValue();
        int maxYear = ((Number) result[1]).intValue();

        Map<String, Integer> years = new HashMap<>();
        years.put("minYear", minYear);
        years.put("maxYear", maxYear);

        return years;
    }

    public Map<String, Object> getMonthlyRevenueAndPurchase(String team, String department, int year) {
        List<Object[]> result = ordersRepository.findMonthlyRevenueAndPurchaseByTeamAndDepartment(team, department, year);

        List<String> labels = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        List<Double> purchases = new ArrayList<>();

        for (Object[] row : result) {
            labels.add((String) row[0]); // month

            // Float 값을 Double로 변환
            revenues.add(((Number) row[1]).doubleValue()); // totalRevenue
            purchases.add(((Number) row[2]).doubleValue()); // totalPurchase
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("revenues", revenues);
        response.put("purchases", purchases);

        return response;
    }

    //대시보드 order 현황
    public List<Map<String, Object>> getTeamOrdersGroupedByEmployee(String team, LocalDate startDate, LocalDate endDate) {
        if (team == null || team.isEmpty()) {
            throw new IllegalArgumentException("Team cannot be null or empty");
        }

        Team teamEnum;
        try {
            teamEnum = Team.valueOf(team.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid team value: " + team);
        }

        return ordersRepository.findTeamOrdersGroupedByEmployee(teamEnum, startDate, endDate).stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("employeeName", result[0]);
                    map.put("totalOrders", ((Number) result[1]).longValue());
                    map.put("completedOrders", ((Number) result[2]).longValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDepartmentOrdersGroupedByEmployee(String department, LocalDate startDate, LocalDate endDate) {
        if (department == null || department.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty");
        }

        Dept departmentEnum;
        try {
            departmentEnum = Dept.valueOf(department.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid department value: " + department);
        }

        return ordersRepository.findDepartmentOrdersGroupedByEmployee(departmentEnum, startDate, endDate).stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("employeeName", result[0]);
                    map.put("totalOrders", ((Number) result[1]).longValue());
                    map.put("completedOrders", ((Number) result[2]).longValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
