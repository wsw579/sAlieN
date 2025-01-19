package com.aivle.project.service;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import com.aivle.project.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    // Create
    public void createOrder(OrdersDto dto) {
        OrdersEntity orderEntity = new OrdersEntity();

        orderEntity.setOrderDate(dto.getOrderDate());
        orderEntity.setSalesDate(dto.getSalesDate());
        orderEntity.setOrderAmount(dto.getOrderAmount());
        orderEntity.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        orderEntity.setContractId(dto.getContractId());
        orderEntity.setProductId(dto.getProductId());
        ordersRepository.save(orderEntity);
    }

    // Read
    public Page<OrdersEntity> readOrders(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            try {
                return ordersRepository.findByOrderIdLike(search, pageable);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 경우 빈 페이지 반환
                return Page.empty(pageable);
            }
        } else {
            return ordersRepository.findAll(pageable);
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
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> results = ordersRepository.countOrdersByStatus();

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

        Map<String, List<Integer>> chartData = new HashMap<>();
        chartData.put("lastYearData", lastYearData);
        chartData.put("currentYearData", currentYearData);

        return chartData;
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
}
