package com.aivle.project.service;

import com.aivle.project.dto.HistoryDto;
import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.*;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.HistoryRepository;
import com.aivle.project.repository.OpportunitiesCommentRepository;
import com.aivle.project.repository.OpportunitiesRepository;
import com.aivle.project.utils.UserContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class OpportunitiesService {

    private final OpportunitiesRepository opportunitiesRepository;
    private final OpportunitiesCommentRepository opportunitiesCommentRepository;
    private final HistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(OpportunitiesService.class);

    // Create
    public void createOpportunities(OpportunitiesDto dto) {
        opportunitiesRepository.save(convertDtoToEntity(dto));
    }

    // Read
    public Page<OpportunitiesEntity> readOpportunities(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));
        if (search != null && !search.isEmpty()) {
            return opportunitiesRepository.findByOpportunityIdLike("%" + search + "%", pageable);
        }
        return opportunitiesRepository.findAll(pageable);
    }

    // Update
    public void updateOpportunities(Long opportunityId, OpportunitiesDto dto) {
        OpportunitiesEntity entity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
        updateEntityFromDto(entity, dto);
        opportunitiesRepository.save(entity);
    }

    // Delete
    public void deleteOpportunities(Long opportunityId) {
        opportunitiesRepository.deleteById(opportunityId);
    }

    public void deleteOpportunitiesByIds(List<Long> ids) {
        opportunitiesRepository.deleteAllById(ids);
    }

    // Search
    public OpportunitiesEntity searchOpportunities(Long opportunityId) {
        return opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
    }

    // Comment Operations
    public List<OpportunitiesCommentEntity> getCommentsByOpportunityId(Long opportunityId) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        return opportunitiesCommentRepository.findByOpportunity(opportunity);
    }

    public void createComment(String content, Long opportunityId, String author) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        OpportunitiesCommentEntity comment = new OpportunitiesCommentEntity(content, LocalDateTime.now(), author, opportunity);
        opportunitiesCommentRepository.save(comment);
    }

    // History Operations
    public List<HistoryEntity> getHistoryByOpportunityId(Long opportunityId) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        return historyRepository.findByOpportunityOrderByDateTimeDesc(opportunity);
    }

    // History calendar
    public List<HistoryEntity> getHistoriesByEmployeeId(String employeeId) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employeeId: " + employeeId));
        List<OpportunitiesEntity> opportunities = opportunitiesRepository.findByEmployeeId(employee);

        List<HistoryEntity> histories = new ArrayList<>();
        for (OpportunitiesEntity opportunity : opportunities) {
            List<HistoryEntity> opportunityHistories = historyRepository.findByOpportunity(opportunity);
            histories.addAll(opportunityHistories);
        }

        return histories;

    }

    public void createHistory(HistoryDto dto) {
        HistoryEntity historyEntity = convertDtoToHistoryEntity(dto);
        historyRepository.save(historyEntity);
    }

    public void updateHistory(Long historyId, HistoryDto dto) {
        HistoryEntity historyEntity = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("History not found"));
        updateHistoryEntityFromDto(historyEntity, dto);
        historyRepository.save(historyEntity);
    }

    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }

    // Utility Methods
    public List<OpportunitiesDto> getAllOpportunityIdsAndNames() {
        List<Object[]> results = opportunitiesRepository.findAllOpportunityIdAndOpportunityName();
        return results.stream()
                .map(result -> {
                    OpportunitiesDto dto = new OpportunitiesDto();
                    dto.setOpportunityId((Long) result[0]);
                    dto.setOpportunityName((String) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Long> getOpportunitiesStatusCounts() {
        return opportunitiesRepository.countAllStatuses()
                .stream()
                .collect(Collectors.toMap(result -> (String) result[0], result -> (Long) result[1]));
    }
    // 내 팀의 상태 수 세기
    public Map<String, Long> getOpportunitiesStatusCountsTeam() {
        String userid = UserContext.getCurrentUserId();
        String userteam = employeeRepository.findTeamById(userid);
        return opportunitiesRepository.countAllStatusesTeam(Team.valueOf(userteam))
                .stream()
                .collect(Collectors.toMap(result -> (String) result[0], result -> (Long) result[1]));
    }

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
        opportunitiesRepository.getMonthlyOpportunities(year)
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

    private OpportunitiesEntity convertDtoToEntity(OpportunitiesDto dto) {
        OpportunitiesEntity entity = new OpportunitiesEntity();
        updateEntityFromDto(entity, dto);
        return entity;
    }

    private void updateEntityFromDto(OpportunitiesEntity entity, OpportunitiesDto dto) {
        entity.setOpportunityName(dto.getOpportunityName());
        entity.setRegion(dto.getRegion());
        entity.setCompanySize(dto.getCompanySize());
        entity.setOpportunityInquiries(dto.getOpportunityInquiries());
        entity.setCustomerEmployee(dto.getCustomerEmployee());
        entity.setQuantity(dto.getQuantity());
        entity.setExpectedRevenue(dto.getExpectedRevenue());
        entity.setCompanyRevenue(dto.getCompanyRevenue());
        entity.setOpportunityNotes(dto.getOpportunityNotes());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setTargetCloseDate(dto.getTargetCloseDate());
        entity.setOpportunityStatus(dto.getOpportunityStatus());
        entity.setSuccessRate(dto.getSuccessRate());
        entity.setLeadId(dto.getLeadId());
        entity.setAccountId(dto.getAccountId());
        entity.setProductId(dto.getProductId());
        entity.setEmployeeId(dto.getEmployeeId());
    }

    private HistoryEntity convertDtoToHistoryEntity(HistoryDto dto) {
        HistoryEntity entity = new HistoryEntity();
        updateHistoryEntityFromDto(entity, dto);
        return entity;
    }

    private void updateHistoryEntityFromDto(HistoryEntity entity, HistoryDto dto) {
        entity.setHistoryTitle(dto.getHistoryTitle());
        entity.setCustomerRepresentative(dto.getCustomerRepresentative());
        entity.setHistoryDate(dto.getHistoryDate());
        entity.setHistoryTime(dto.getHistoryTime());
        entity.setCompanySize(dto.getCompanySize());
        entity.setMeetingPlace(dto.getMeetingPlace());
        entity.setActionTaken(dto.getActionTaken());
        entity.setCustomerRequirements(dto.getCustomerRequirements());
        entity.setOpportunity(dto.getOpportunityId());
    }


    public Map<String, Object> getSalesData(String teamId, String departmentId) {

        // 적절한 데이터 가져오기
        List<Map<String, Object>> opportunities;
        if (teamId != null) {
            opportunities = getOpportunitiesByTeam(teamId);
        } else {
            opportunities = getOpportunitiesByDepartment(departmentId);
        }

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("labels", opportunities.stream().map(data -> data.get("employeeName")).collect(Collectors.toList()));
        response.put("values", opportunities.stream().map(data -> data.get("opportunityCount")).collect(Collectors.toList()));

        return response;
    }

    private List<Map<String, Object>> getOpportunitiesByTeam(String team) {
        List<Object[]> results = opportunitiesRepository.findTop5ByTeamWithCount(team);
        return mapOpportunities(results);
    }

    private List<Map<String, Object>> getOpportunitiesByDepartment(String dept) {
        List<Object[]> results = opportunitiesRepository.findTop5ByDepartmentWithCount(dept);
        return mapOpportunities(results);
    }

    // 데이터를 맵으로 변환
    private List<Map<String, Object>> mapOpportunities(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("employeeName", result[0]);
            map.put("opportunityCount", result[1]);
            return map;
        }).collect(Collectors.toList());
    }



}