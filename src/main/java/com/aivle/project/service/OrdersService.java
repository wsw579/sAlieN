package com.aivle.project.service;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.OrdersRepository;
import com.aivle.project.utils.UserContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final EmployeeRepository employeeRepository;

    // Create
    public void createOrder(OrdersDto dto) {
        // 현재 사용자 정보 가져오기
        String currentUserId = UserContext.getCurrentUserId();
        System.out.println("현재 로그인된 사용자 ID: " + currentUserId);
        // 데이터베이스에서 EmployeeEntity 로드
        EmployeeEntity employee = employeeRepository.findByEmployeeId(currentUserId);

        OrdersEntity orderEntity = new OrdersEntity();

        orderEntity.setOrderDate(dto.getOrderDate());
        orderEntity.setSalesDate(dto.getSalesDate());
        orderEntity.setOrderAmount(dto.getOrderAmount());
        orderEntity.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        orderEntity.setContractId(dto.getContractId());
        orderEntity.setProductId(dto.getProductId());
        orderEntity.setEmployeeId(employee);
        ordersRepository.save(orderEntity);
    }

    // Read
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
        OrdersEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        System.out.println("Before update: " + order);

        order.setOrderDate(dto.getOrderDate());
        order.setSalesDate(dto.getSalesDate());
        order.setOrderAmount(dto.getOrderAmount());
        order.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        order.setContractId(dto.getContractId());
        order.setProductId(dto.getProductId());
        ordersRepository.save(order);
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

    // 상태 수 가져오기
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

    public Map<String, List<Integer>> getBarData() {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        // 각 월별 주문 수를 초기화
        List<Integer> lastYearData = IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());
        List<Integer> currentYearData = IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());

        // DB에서 월별 주문 수를 가져옵니다.
        List<Object[]> lastYearOrders = ordersRepository.getMonthlyOrders(lastYear);
        List<Object[]> currentYearOrders = ordersRepository.getMonthlyOrders(currentYear);

        // 결과를 리스트에 추가
        for (Object[] row : lastYearOrders) {
            int month = ((Number) row[0]).intValue() - 1; // 월 (1월 = 0 인덱스)
            int count = ((Number) row[1]).intValue(); // 주문 수
            lastYearData.set(month, count);
        }

        for (Object[] row : currentYearOrders) {
            int month = ((Number) row[0]).intValue() - 1; // 월 (1월 = 0 인덱스)
            int count = ((Number) row[1]).intValue(); // 주문 수
            currentYearData.set(month, count);
        }

        // 누적 값 계산
        for (int i = 1; i < 12; i++) {
            lastYearData.set(i, lastYearData.get(i) + lastYearData.get(i - 1));
            currentYearData.set(i, currentYearData.get(i) + currentYearData.get(i - 1));
        }

        Map<String, List<Integer>> barData = new HashMap<>();
        barData.put("lastYearData", lastYearData);
        barData.put("currentYearData", currentYearData);

        return barData;
    }


    public Map<String, List<Integer>> getChartData() {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        // 각 월별 주문 수를 초기화
        List<Integer> lastYearData = IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());
        List<Integer> currentYearData = IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());

        // DB에서 월별 주문 수를 가져옵니다.
        List<Object[]> lastYearOrders = ordersRepository.getMonthlyOrders(lastYear);
        List<Object[]> currentYearOrders = ordersRepository.getMonthlyOrders(currentYear);

        // 결과를 리스트에 추가
        for (Object[] row : lastYearOrders) {
            int month = ((Number) row[0]).intValue() - 1; // 월 (1월 = 0 인덱스)
            int count = ((Number) row[1]).intValue(); // 주문 수
            lastYearData.set(month, count);
        }

        for (Object[] row : currentYearOrders) {
            int month = ((Number) row[0]).intValue() - 1; // 월 (1월 = 0 인덱스)
            int count = ((Number) row[1]).intValue(); // 주문 수
            currentYearData.set(month, count);
        }

        Map<String, List<Integer>> chartData = new HashMap<>();
        chartData.put("lastYearData", lastYearData);
        chartData.put("currentYearData", currentYearData);

        return chartData;
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
}
