package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.dto.LeadsDto;
import com.aivle.project.dto.ProductsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.LeadsRepository;

import com.aivle.project.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EmployeeRepository employeeRepository;

    // Create
    // by mapping data from a LeadsDto to a LeadsEntity

    // Declares a public method named createLeads that takes a LeadsDto object as input
    public void createLeads(LeadsDto dto){
        //Creates a new instance of LeadsEntity to store data that will eventually be saved in the database.
        LeadsEntity leadsEntity = convertDtoToEntity(dto);
        leadsRepository.save(leadsEntity);
    }


    //Read
    @Transactional(readOnly = true)
    public Page<LeadsEntity> readLeads(int page, int size, String search, String sortColumn, String sortDirection) {
        String userid = UserContext.getCurrentUserId();
        String userrole = UserContext.getCurrentUserRole();
        String userposition = employeeRepository.findPositionById(userid);
        String userteam = employeeRepository.findTeamById(userid);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if ("ROLE_ADMIN".equals(userrole) || "GENERAL_MANAGER".equals(userposition) || "DEPARTMENT_HEAD".equals(userposition) || "TEAM_LEADER".equals(userposition)) {
            return findLeadsForManager(search, pageable);
        } else if ("ROLE_USER".equals(userrole)) {
            return findLeadsForTeam(search, userteam, pageable);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }


    // Update
    // executes within a database transaction
    // leadId (type Long) -> input : identifies the specific lead to be updated
    public void  updateLeads(Long leadId, LeadsDto dto){
        LeadsEntity leadsEntity = leadsRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
        updateEntityFromDto(leadsEntity, dto);
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
    @Transactional(readOnly = true)
    public Map<String, Long> getLeadStatusCounts() {
        String userid = UserContext.getCurrentUserId();
        String userrole = UserContext.getCurrentUserRole();
        String userposition = employeeRepository.findPositionById(userid);
        String userteam = employeeRepository.findTeamById(userid);

// 적절한 쿼리 실행
        List<Object[]> results = isManager(userrole, userposition)
                ? leadsRepository.countLeadsByStatusManager()
                : leadsRepository.countLeadsByStatusTeam(Team.valueOf(userteam));

// Stream API 활용하여 데이터 변환
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],  // Key: Status
                        result -> (Long) result[1]     // Value: Count
                ));
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
        String userid = UserContext.getCurrentUserId();
        String userrole = UserContext.getCurrentUserRole();
        String userposition = employeeRepository.findPositionById(userid);
        String userteam = employeeRepository.findTeamById(userid);


// 데이터 조회 (관리자는 모든 데이터, 일반 사용자는 팀별 데이터)
        List<Object[]> queryResult = isManager(userrole, userposition)
                ? leadsRepository.getMonthlyLeadsManager(year)
                : leadsRepository.getMonthlyLeadsTeam(year, Team.valueOf(userteam));

// 공통 로직: 데이터 매핑
        queryResult.forEach(row -> {
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
    private LeadsEntity convertDtoToEntity(LeadsDto dto) {
        LeadsEntity leadsEntity = new LeadsEntity();
        updateEntityFromDto(leadsEntity, dto);
        return leadsEntity;
    }

    private void updateEntityFromDto(LeadsEntity entity, LeadsDto dto) {
        entity.setLeadStatus(dto.getLeadStatus());
        entity.setLeadSource(dto.getLeadSource());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setTargetCloseDate(dto.getTargetCloseDate());
        entity.setCustomerRequirements(dto.getCustomerRequirements());
        entity.setCustomerRepresentitive(dto.getCustomerRepresentitive());
        entity.setCompanyName(dto.getCompanyName());
        entity.setC_tel(dto.getC_tel());
        // 외래키 부분
        entity.setAccountId(dto.getAccountId());
        entity.setEmployeeId(dto.getEmployeeId());
    }

    private LeadsDto convertIdToDto(Long id) {
        LeadsDto dto = new LeadsDto();
        dto.setLeadId(id);
        return dto;
    }
    // 오늘 추가된 lead의 수
    public long getTodayLeadsForTeam() {
        String userid = UserContext.getCurrentUserId();
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // Repository 호출하여 데이터 가져오기
        return leadsRepository.countTodayLeadsUser(today, userid);
    }

    // Under Review 상태 세기
    public long countLeadsByStatusAndTeam(String leadStatus) {
        String userid = UserContext.getCurrentUserId();
        return leadsRepository.countLeadsByStatusUser(leadStatus, userid);
    }

    // 오늘 마감인 leads 수 세기
    public long countLeadsWithTargetCloseDateTodayForTeam() {
        String userid = UserContext.getCurrentUserId();
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // Repository 메서드 호출
        return leadsRepository.countLeadsWithTargetCloseDateTodayUser(today, userid);
    }

    // 권한별 조회
    private Page<LeadsEntity> findLeadsForManager(String search, Pageable pageable) {
        // Manager 전용 로직
        if (search != null && !search.isEmpty()) {
            return leadsRepository.findByCompanyNameLikeManager("%" + search + "%", pageable);
        }
        return leadsRepository.findAll(pageable);
    }

    private Page<LeadsEntity> findLeadsForTeam(String search, String teamId, Pageable pageable) {
        // User 전용 로직
        if (search != null && !search.isEmpty()) {
            return leadsRepository.findByCompanyNameLikeTeam("%" + search + "%", Team.valueOf(teamId), pageable);
        }
        return leadsRepository.findByTeamId(Team.valueOf(teamId), pageable);
    }

    private boolean isManager(String userrole, String userposition) {
        return "ROLE_ADMIN".equals(userrole) ||
                "GENERAL_MANAGER".equals(userposition) ||
                "DEPARTMENT_HEAD".equals(userposition) ||
                "TEAM_LEADER".equals(userposition);
    }
}
