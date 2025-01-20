package com.aivle.project.service;

import com.aivle.project.dto.HistoryDto;
import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.entity.OpportunitiesCommentEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.HistoryRepository;
import com.aivle.project.repository.OpportunitiesCommentRepository;
import com.aivle.project.repository.OpportunitiesRepository;
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


@Service
@Transactional
@RequiredArgsConstructor
public class OpportunitiesService {

    private final OpportunitiesRepository opportunitiesRepository;
    private final OpportunitiesCommentRepository opportunitiesCommentRepository;
    private final HistoryRepository historyRepository;

    // Create
    public void createOpportunities(OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = new OpportunitiesEntity();

        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setCustomerEmployee(dto.getCustomerEmployee());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());
        //외래키 부분
        opportunitiesEntity.setLeadId(dto.getLeadId());
        opportunitiesEntity.setAccountId(dto.getAccountId());
        opportunitiesEntity.setProductId(dto.getProductId());
        opportunitiesEntity.setEmployeeId(dto.getEmployeeId());

        opportunitiesRepository.save(opportunitiesEntity);
    }

    // Read
    public Page<OpportunitiesEntity> readOpportunities(int page, int size, String search, String sortColumn, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));

        if (search != null && !search.isEmpty()) {
            try {
                return opportunitiesRepository.findByOpportunityIdLike(search, pageable);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 경우 빈 페이지 반환
                return Page.empty(pageable);
            }
        } else {
            return opportunitiesRepository.findAll(pageable);
        }
    }


    // Update
    @Transactional
    public void updateOpportunities(Long opportunityId, OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setCustomerEmployee(dto.getCustomerEmployee());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());

        //외래키 부분
        opportunitiesEntity.setLeadId(dto.getLeadId());
        opportunitiesEntity.setAccountId(dto.getAccountId());
        opportunitiesEntity.setProductId(dto.getProductId());
        opportunitiesEntity.setEmployeeId(dto.getEmployeeId());

        opportunitiesRepository.save(opportunitiesEntity);

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
                .orElseThrow(()->new IllegalArgumentException("error"));
    }


    // lead comment
    @Transactional
    public List<OpportunitiesCommentEntity> getCommentsByOpportunityId(Long opportunityId) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        List<OpportunitiesCommentEntity> comments = opportunitiesCommentRepository.findByOpportunity(opportunity);

        // 디버깅을 위해 로그 출력
        comments.forEach(comment -> System.out.println("Comment: " + comment.getContent()));

        return comments;
    }

    // create comment
    @Transactional
    public void createComment(String content, Long opportunityId, String author) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        OpportunitiesCommentEntity comment = new OpportunitiesCommentEntity();
        comment.setContent(content);
        comment.setCommentCreatedDate(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setOpportunity(opportunity);
        opportunitiesCommentRepository.save(comment);
    }



    public HistoryEntity searchHistory(Long historyId) {
        return historyRepository.findById(historyId)
                .orElseThrow(()->new IllegalArgumentException("error"));
    }

    // history service
    // Read history
    @Transactional
    public List<HistoryEntity> getHistoryByOpportunityId(Long opportunityId) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        List<HistoryEntity> history = historyRepository.findByOpportunityOrderByDateTimeDesc(opportunity);

        return history;
    }

    // create history
    @Transactional
    public void createHistory(HistoryDto dto) {
        HistoryEntity historyEntity = new HistoryEntity();
        // 데이터베이스 제목 컬럼 하나 추가하기
        historyEntity.setHistoryTitle(dto.getHistoryTitle());
        historyEntity.setCustomerRepresentative(dto.getCustomerRepresentative());
        historyEntity.setHistoryDate(dto.getHistoryDate());
        historyEntity.setHistoryTime(dto.getHistoryTime());
        historyEntity.setCompanySize(dto.getCompanySize());
        historyEntity.setMeetingPlace(dto.getMeetingPlace());
        historyEntity.setActionTaken(dto.getActionTaken());
        historyEntity.setCustomerRequirements(dto.getCustomerRequirements());
        historyEntity.setOpportunity(dto.getOpportunityId());
        historyRepository.save(historyEntity);

    }

    // update history
    @Transactional
    public void updateHistory(Long historyId, HistoryDto dto) {
        HistoryEntity historyEntity = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("History not found"));

        historyEntity.setHistoryTitle(dto.getHistoryTitle());
        historyEntity.setCustomerRepresentative(dto.getCustomerRepresentative());
        historyEntity.setHistoryDate(dto.getHistoryDate());
        historyEntity.setHistoryTime(dto.getHistoryTime());
        historyEntity.setCompanySize(dto.getCompanySize());
        historyEntity.setMeetingPlace(dto.getMeetingPlace());
        historyEntity.setActionTaken(dto.getActionTaken());
        historyEntity.setCustomerRequirements(dto.getCustomerRequirements());
        historyEntity.setOpportunity(dto.getOpportunityId());
        historyRepository.save(historyEntity);

    }

    // history delete
    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }

    // 상태 수 가져오기
    public Map<String, Long> getOpportunitiesStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();

        // 쿼리 결과 가져오기
        List<Object[]> results = opportunitiesRepository.countAllStatuses();

        // 결과 매핑
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
        List<Object[]> lastYearOrders = opportunitiesRepository.getMonthlyOpportunities(lastYear);
        List<Object[]> currentYearOrders = opportunitiesRepository.getMonthlyOpportunities(currentYear);

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
        List<Object[]> lastYearOrders = opportunitiesRepository.getMonthlyOpportunities(lastYear);
        List<Object[]> currentYearOrders = opportunitiesRepository.getMonthlyOpportunities(currentYear);

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