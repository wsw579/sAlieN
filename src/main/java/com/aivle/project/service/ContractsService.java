package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@Transactional
@RequiredArgsConstructor
public class ContractsService {

    private final ContractsRepository contractsRepository;
    private final OrdersRepository ordersRepository;

    // Create
    public void createContracts(ContractsDto dto) {
        ContractsEntity contractsEntity = new ContractsEntity();

        contractsEntity.setContractStatus(dto.getContractStatus());
        contractsEntity.setStartDate(dto.getStartDate());
        contractsEntity.setTerminationDate(dto.getTerminationDate());
        contractsEntity.setContractDetail(dto.getContractDetail());
        contractsEntity.setContractSales(dto.getContractSales());
        contractsEntity.setContractAmount(dto.getContractAmount());
        contractsEntity.setContractClassification(dto.getContractClassification());

        contractsEntity.setOpportunityId(dto.getOpportunityId());
        contractsEntity.setAccountId(dto.getAccountId());
        contractsEntity.setProductId(dto.getProductId());
        contractsEntity.setEmployeeId(dto.getEmployeeId());
        contractsEntity.setOpportunityId((dto.getOpportunityId()));
        contractsRepository.save(contractsEntity);
    }

    // Read
    public Page<ContractsEntity> readContracts(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            try {
                return contractsRepository.findByContractIdLike(search, pageable);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 경우 빈 페이지 반환
                return Page.empty(pageable);
            }
        } else {
            return contractsRepository.findAll(pageable);
        }
    }


    // Update
    @Transactional
    public void updateContracts(Long contractId, ContractsDto dto) {
        ContractsEntity contractsEntity = contractsRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contractsEntity.setContractStatus(dto.getContractStatus());
        contractsEntity.setStartDate(dto.getStartDate());
        contractsEntity.setTerminationDate(dto.getTerminationDate());
        contractsEntity.setContractDetail(dto.getContractDetail());
        contractsEntity.setContractSales(dto.getContractSales());
        contractsEntity.setContractAmount(dto.getContractAmount());
        contractsEntity.setContractClassification(dto.getContractClassification());

        contractsEntity.setOpportunityId(dto.getOpportunityId());
        contractsEntity.setAccountId(dto.getAccountId());
        contractsEntity.setProductId(dto.getProductId());
        contractsEntity.setEmployeeId(dto.getEmployeeId());
        contractsEntity.setOpportunityId((dto.getOpportunityId()));

        contractsRepository.save(contractsEntity);

    }


    // Delete
    public void deleteContracts(Long contractId) {
        contractsRepository.deleteById(contractId);
    }



    public void deleteContractsByIds(List<Long> ids) {
        contractsRepository.deleteAllById(ids);
    }



    // Search
    public ContractsEntity searchContracts(Long contractId) {
        return contractsRepository.findById(contractId)
                .orElseThrow(()->new IllegalArgumentException("error"));
    }


    // lead order
    @Transactional
    public List<OrdersEntity> getOrdersByContractId(Long contractId) {
        ContractsEntity contract = searchContracts(contractId);
        List<OrdersEntity> orders = ordersRepository.findByContractId(contract);

        // 디버깅을 위해 로그 출력
        //orders.forEach(comment -> System.out.println("Order: " + orders.getorderId()));

        return orders;
    }

    // detail 페이지 select 로딩을 위한 id와 name 가져오기
    public List<ContractsDto> getAllContractIds() {
        List<Long> results = contractsRepository.findAllContractIds();
        return results.stream()
                .map(result -> {
                    ContractsDto dto = new ContractsDto();
                    dto.setContractId(result);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 상태 수 가져오기
    public Map<String, Long> getContractStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> results = contractsRepository.countContractsByStatus();

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
        List<Object[]> lastYearOrders = contractsRepository.getMonthlyContracts(lastYear);
        List<Object[]> currentYearOrders = contractsRepository.getMonthlyContracts(currentYear);

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
        List<Object[]> lastYearOrders = contractsRepository.getMonthlyContracts(lastYear);
        List<Object[]> currentYearOrders = contractsRepository.getMonthlyContracts(currentYear);

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
