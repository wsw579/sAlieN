package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Transactional
@RequiredArgsConstructor
public class ContractsService {

    private final ContractsRepository contractsRepository;
    private final OrdersRepository ordersRepository;
    private static final Logger logger = LoggerFactory.getLogger(ContractsService.class);

    // Create
    public void createContracts(ContractsDto dto) {
        ContractsEntity contractsEntity = convertDtoToEntity(dto);
        contractsRepository.save(contractsEntity);
    }

    // Read
    @Transactional(readOnly = true)
    public Page<ContractsEntity> readContracts(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            return contractsRepository.findByContractIdLike("%" + search + "%", pageable);
        }
        return contractsRepository.findAll(pageable);
    }

    // Update
    public void updateContracts(Long contractId, ContractsDto dto) {
        ContractsEntity contractsEntity = contractsRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        updateEntityFromDto(contractsEntity, dto);
        contractsRepository.save(contractsEntity);
    }

    // Delete
    public void deleteContracts(Long contractId) {
        contractsRepository.deleteById(contractId);
    }

    public void deleteContractsByIds(List<Long> ids) {
        contractsRepository.deleteAllById(ids);
    }

    // 테이블 데이터 전달
    @Transactional(readOnly = true)
    public Map<String, Object> getContractPageData(int page, int size, String search, String sortColumn, String sortDirection) {
        Page<ContractsEntity> contractsPage = readContracts(page, size, search, sortColumn, sortDirection);
        Map<String, Long> statusCounts = getContractStatusCounts();

        Map<String, Object> data = new HashMap<>();
        data.put("contractsPage", contractsPage);
        data.put("statusCounts", statusCounts);
        data.put("totalCount", statusCounts.values().stream().mapToLong(Long::longValue).sum());

        return data;
    }



    // 상태별 카운트 가져오기
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ContractsEntity searchContracts(Long contractId) {
        logger.info("Searching for contract with ID: {}", contractId);
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        return contractsRepository.findById(contractId)
                .orElseThrow(() -> {
                    logger.error("Contract not found with ID: {}", contractId);
                    return new IllegalArgumentException("Contract not found with ID: " + contractId);
                });
    }

    // 주문 정보 가져오기
    @Transactional(readOnly = true)
    public List<OrdersEntity> getOrdersByContractId(Long contractId) {
        logger.info("Fetching orders for contract ID: {}", contractId);
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        ContractsEntity contract = searchContracts(contractId);
        return ordersRepository.findByContractId(contract);
    }

    // ID 가져오기
    @Transactional(readOnly = true)
    public List<ContractsDto> getAllContractIds() {
        List<Long> results = contractsRepository.findAllContractIds();
        return results.stream()
                .map(this::convertIdToDto)
                .collect(Collectors.toList());
    }

    // Bar 및 Chart Data
    @Transactional(readOnly = true)
    public Map<String, List<Integer>> getBarData() {
        return getYearlyData(true); // 누적 데이터 포함
    }

    @Transactional(readOnly = true)
    public Map<String, List<Integer>> getChartData() {
        return getYearlyData(false); // 누적 데이터 제외
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
        List<Object[]> orders = contractsRepository.getMonthlyContracts(year);
        for (Object[] row : orders) {
            int month = ((Number) row[0]).intValue() - 1; // 1월 = 0
            int count = ((Number) row[1]).intValue();
            monthlyData.set(month, count);
        }
    }

    private void accumulateMonthlyData(List<Integer> monthlyData) {
        for (int i = 1; i < monthlyData.size(); i++) {
            monthlyData.set(i, monthlyData.get(i) + monthlyData.get(i - 1));
        }
    }

    // 헬퍼 메서드
    private ContractsEntity convertDtoToEntity(ContractsDto dto) {
        ContractsEntity contractsEntity = new ContractsEntity();
        updateEntityFromDto(contractsEntity, dto);
        return contractsEntity;
    }

    private void updateEntityFromDto(ContractsEntity entity, ContractsDto dto) {
        entity.setContractStatus(dto.getContractStatus());
        entity.setStartDate(dto.getStartDate());
        entity.setTerminationDate(dto.getTerminationDate());
        entity.setContractDetail(dto.getContractDetail());
        entity.setContractSales(dto.getContractSales());
        entity.setContractAmount(dto.getContractAmount());
        entity.setContractClassification(dto.getContractClassification());
        entity.setOpportunityId(dto.getOpportunityId());
        entity.setAccountId(dto.getAccountId());
        entity.setProductId(dto.getProductId());
        entity.setEmployeeId(dto.getEmployeeId());
    }

    private ContractsDto convertIdToDto(Long id) {
        ContractsDto dto = new ContractsDto();
        dto.setContractId(id);
        return dto;
    }
}
