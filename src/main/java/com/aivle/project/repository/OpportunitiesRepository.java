package com.aivle.project.repository;

import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface OpportunitiesRepository extends JpaRepository<OpportunitiesEntity, Long> {

    OpportunitiesEntity findByOpportunityId(Long opportunityId);

    @Query("SELECT o.opportunityId, o.opportunityName FROM OpportunitiesEntity o")
    List<Object[]> findAllOpportunityIdAndOpportunityName();

    @Query("SELECT o FROM OpportunitiesEntity o WHERE CAST(o.opportunityId AS string) LIKE %:opportunityId%")
    Page<OpportunitiesEntity> findByOpportunityIdLike(@Param("opportunityId") String opportunityId, Pageable pageable);

    @Query("SELECT " +
            "CASE " +
            "   WHEN o.targetCloseDate < CURRENT_DATE AND o.opportunityStatus NOT LIKE 'Closed%' THEN 'Overdue' " +
            "   WHEN o.opportunityStatus = 'Pending' THEN 'Pending' " +
            "   WHEN o.opportunityStatus LIKE 'Closed%' THEN 'Closed' " +
            "   WHEN o.opportunityStatus NOT LIKE 'Closed%' THEN 'Ongoing' " +
            "END AS status, " +
            "COUNT(o) " +
            "FROM OpportunitiesEntity o " +
            "GROUP BY " +
            "CASE " +
            "   WHEN o.targetCloseDate < CURRENT_DATE AND o.opportunityStatus NOT LIKE 'Closed%' THEN 'Overdue' " +
            "   WHEN o.opportunityStatus = 'Pending' THEN 'Pending' " +
            "   WHEN o.opportunityStatus LIKE 'Closed%' THEN 'Closed' " +
            "   WHEN o.opportunityStatus NOT LIKE 'Closed%' THEN 'Ongoing' " +
            "END")
    List<Object[]> countAllStatuses();
    // 이번달 기회 상태 수
    @Query("SELECT " +
            "CASE " +
            "   WHEN o.targetCloseDate < CURRENT_DATE AND o.opportunityStatus NOT LIKE 'Closed%' THEN 'Overdue' " +
            "   WHEN o.opportunityStatus = 'Pending' THEN 'Pending' " +
            "   WHEN o.opportunityStatus LIKE 'Closed%' THEN 'Closed' " +
            "   WHEN o.opportunityStatus NOT LIKE 'Closed%' THEN 'Ongoing' " +
            "END AS status, " +
            "COUNT(o) " +
            "FROM OpportunitiesEntity o " +
            "JOIN o.employeeId e " +  // Employee와 JOIN
            "WHERE e.employeeId = :employeeId " + // employeeId 조건 추가
            "AND MONTH(o.createdDate) = MONTH(CURRENT_DATE) " + // 같은 달 조건
            "AND YEAR(o.createdDate) = YEAR(CURRENT_DATE) " +   // 같은 연도 조건
            "GROUP BY " +
            "CASE " +
            "   WHEN o.targetCloseDate < CURRENT_DATE AND o.opportunityStatus NOT LIKE 'Closed%' THEN 'Overdue' " +
            "   WHEN o.opportunityStatus = 'Pending' THEN 'Pending' " +
            "   WHEN o.opportunityStatus LIKE 'Closed%' THEN 'Closed' " +
            "   WHEN o.opportunityStatus NOT LIKE 'Closed%' THEN 'Ongoing' " +
            "END")
    List<Object[]> countAllStatusesUser(@Param("employeeId") String employeeId);



    // 차트 그래프
    @Query("SELECT MONTH(o.createdDate), COUNT(o) " +
            "FROM OpportunitiesEntity o " +
            "WHERE YEAR(o.createdDate) = :year AND o.opportunityStatus = 'Closed(won)' " +
            "GROUP BY MONTH(o.createdDate)")
    List<Object[]> getMonthlyOpportunities(@Param("year") int year);


    // calendar
    List<OpportunitiesEntity> findByEmployeeId(EmployeeEntity employeeId);

    // 직원 조회
    @Query("SELECT COUNT(o) FROM OpportunitiesEntity o WHERE o.employeeId.employeeId = :employeeId AND o.opportunityStatus IN ('Qualification', 'Needs Analysis', 'Proposal', 'Negotiation')")
    long countByEmployeeIdAndStatus(@Param("employeeId") String employeeId);

    @Query("SELECT o FROM OpportunitiesEntity o WHERE o.employeeId.teamId = :team")
    List<OpportunitiesEntity> findByTeam(@Param("team") Team team);

    @Query("SELECT o FROM OpportunitiesEntity o WHERE o.employeeId.departmentId = :dept")
    List<OpportunitiesEntity> findByDepartment(@Param("dept") Dept dept);

    // 사원의 진행중 히스토리 그래프 - 보류
//    @Query("SELECT o.opportunityName, " +
//            "       COALESCE(COUNT(h), 0) " +
//            "FROM OpportunitiesEntity o " +
//            "LEFT JOIN HistoryEntity h ON h.opportunity = o " +
//            "  AND o.opportunityStatus IN ('Qualification', 'Needs Analysis', 'Proposal', 'Negotiation') " +
//            "WHERE o.employeeId.employeeId = :employeeId " +
//            "GROUP BY o.opportunityId, o.opportunityName")
//    List<Object[]> countHistoryByEmployeeId(@Param("employeeId") String employeeId);




}

