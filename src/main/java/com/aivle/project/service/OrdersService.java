package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.entity.ProductsEntity;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import com.aivle.project.repository.ProductsRepository;
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


    // Create
    public void createOrder(OrdersDto dto) {
        OrdersEntity orderEntity = convertDtoToEntity(dto);
        ordersRepository.save(orderEntity);
    }

    // Read
    @Transactional(readOnly = true)
    public Page<OrdersEntity> readOrders(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            return ordersRepository.findByOrderIdLike("%" + search + "%", pageable);
        }
        return ordersRepository.findAll(pageable);
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
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> results = ordersRepository.countOrdersByStatus();

        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
        }

        return statusCounts;
    }

    // Bar 및 Chart Data
    public Map<String, List<Integer>> getBarData() {
        return getYearlyData(true);
    }

    public Map<String, List<Integer>> getChartData() {
        return getYearlyData(false);
    }

    private Map<String, List<Integer>> getYearlyData(boolean accumulate) {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        List<Integer> lastYearData = initializeMonthlyData();
        List<Integer> currentYearData = initializeMonthlyData();

        populateMonthlyData(lastYear, lastYearData);
        populateMonthlyData(currentYear, currentYearData);

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
        entity.setOrderDate(dto.getOrderDate());
        entity.setSalesDate(dto.getSalesDate());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderStatus(OrderStatus.valueOf(dto.getOrderStatus()));
        entity.setContractId(dto.getContractId());
        entity.setProductId(dto.getProductId());
        ordersRepository.save(entity);
    }
}
