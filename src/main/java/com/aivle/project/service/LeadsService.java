package com.aivle.project.service;

import com.aivle.project.dto.LeadsDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.LeadsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


// A service layer component that contains business logic
@Service
@Transactional
@RequiredArgsConstructor
public class LeadsService {
    // Dependency Injection
    private final LeadsRepository leadsRepository;

    // Create
    // by mapping data from a LeadsDto to a LeadsEntity

    // Declares a public method named createLeads that takes a LeadsDto object as input
    public void createLeads(LeadsDto dto){
        //Creates a new instance of LeadsEntity to store data that will eventually be saved in the database.
        LeadsEntity leadsEntity = new LeadsEntity();

        //calls each method(setLeadStatus, setLeadSource...) on the leadsEntity object to assign a value to its field
        //the value is retrieved from the LeadsDto object using dto.get___()
        leadsEntity.setLeadStatus(dto.getLeadStatus());
        leadsEntity.setLeadSource(dto.getLeadSource());
        leadsEntity.setCreatedDate(dto.getCreatedDate());
        leadsEntity.setTargetCloseDate(dto.getTargetCloseDate());
        leadsEntity.setCustomerRequirements(dto.getCustomerRequirements());
        leadsEntity.setCustomerRepresentitive(dto.getCustomerRepresentitive());
        leadsEntity.setCompanyName(dto.getCompanyName());
        leadsEntity.setC_tel(dto.getC_tel());
        // 외래키 부분
        leadsEntity.setAccountId(dto.getAccountId());
        leadsEntity.setEmployeeId(dto.getEmployeeId());
        //Saves the leadsEntity object to the database using the leadsRepository
        leadsRepository.save(leadsEntity);
    }


    //Read
    public Page<LeadsEntity> readLeads(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            try {
                return leadsRepository.findByLeadIdLike(search, pageable);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 경우 빈 페이지 반환
                return Page.empty(pageable);
            }
        } else {
            return leadsRepository.findAll(pageable);
        }
    }


    // Update
    // executes within a database transaction
    @Transactional

    // leadId (type Long) -> input : identifies the specific lead to be updated
    public void  updateLeads(Long leadId, LeadsDto dto){
        LeadsEntity leadsEntity = leadsRepository.findByLeadId(leadId);
                // throwing a custom exception:IllegalArgumentException("message to show")
            if (leadsEntity == null) {
                throw new IllegalArgumentException("Leads not found");
            } // .orElseThrow(() -> new IllegalArgumentException("Leads not found"));
        leadsEntity.setLeadStatus(dto.getLeadStatus());
        leadsEntity.setLeadSource(dto.getLeadSource());
        leadsEntity.setCreatedDate(dto.getCreatedDate());
        leadsEntity.setTargetCloseDate(dto.getTargetCloseDate());
        leadsEntity.setCustomerRequirements(dto.getCustomerRequirements());
        leadsEntity.setCustomerRepresentitive(dto.getCustomerRepresentitive());
        leadsEntity.setCompanyName(dto.getCompanyName());
        leadsEntity.setC_tel(dto.getC_tel());

        //외래키 부분
        leadsEntity.setAccountId(dto.getAccountId());
        leadsEntity.setEmployeeId(dto.getEmployeeId());
        leadsRepository.save(leadsEntity);
    }

    // Delete
    public void deleteLeads(Long leadId){
        leadsRepository.deleteById(leadId);
    }

    public void deleteLeadsByIds(List<Long> ids){
        leadsRepository.deleteAllById(ids);
    }

    //Search by lead id
    public LeadsEntity searchLeads(Long leadId) {
        LeadsEntity leadsEntity = leadsRepository.findByLeadId(leadId);
        if (leadsEntity == null) {
            throw new IllegalArgumentException("No leads found");
        }

        return leadsEntity;  // This is called after the null check
    }

    // detail 페이지 select 로딩을 위한 id와 name 가져오기
    public List<LeadsDto> getAllLeadIdsAndCompanyNames() {
        List<Object[]> results = leadsRepository.findAllLeadIdAndCompanyName();
        return results.stream()
                .map(result -> {
                    LeadsDto dto = new LeadsDto();
                    dto.setLeadId((Long) result[0]);
                    dto.setCompanyName((String) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 상태 수 가져오기
    public Map<String, Long> getLeadStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();
        List<Object[]> results = leadsRepository.countLeadsByStatus();

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
        List<Object[]> lastYearOrders = leadsRepository.getMonthlyLeads(lastYear);
        List<Object[]> currentYearOrders = leadsRepository.getMonthlyLeads(currentYear);

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
        List<Object[]> lastYearOrders = leadsRepository.getMonthlyLeads(lastYear);
        List<Object[]> currentYearOrders = leadsRepository.getMonthlyLeads(currentYear);

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
