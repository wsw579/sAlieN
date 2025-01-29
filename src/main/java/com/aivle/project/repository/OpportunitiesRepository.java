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

    @Query("SELECT o FROM OpportunitiesEntity o WHERE o.opportunityName LIKE %:opportunityName%")
    Page<OpportunitiesEntity> findByOpportunityNameLikeManager(@Param("opportunityName") String opportunityName, Pageable pageable);

    @Query("SELECT o FROM OpportunitiesEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND o.opportunityName LIKE %:opportunityName%")
    Page<OpportunitiesEntity> findByOpportunityNameLikeUser(
            @Param("opportunityName") String opportunityName,
            @Param("teamId") Team teamId,
            Pageable pageable);

    @Query("SELECT o FROM OpportunitiesEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId")
    Page<OpportunitiesEntity> findByTeamId(
            @Param("teamId") Team teamId,
            Pageable pageable);

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
            "WHERE e.teamId = :teamId " + // employeeId 조건 추가
            "GROUP BY " +
            "CASE " +
            "   WHEN o.targetCloseDate < CURRENT_DATE AND o.opportunityStatus NOT LIKE 'Closed%' THEN 'Overdue' " +
            "   WHEN o.opportunityStatus = 'Pending' THEN 'Pending' " +
            "   WHEN o.opportunityStatus LIKE 'Closed%' THEN 'Closed' " +
            "   WHEN o.opportunityStatus NOT LIKE 'Closed%' THEN 'Ongoing' " +
            "END")
    List<Object[]> countAllStatusesUserTeam(@Param("teamId") Team teamId);

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
    List<Object[]> getMonthlyOpportunitiesManager(@Param("year") int year);

    // 차트 그래프
    @Query("SELECT MONTH(o.createdDate), COUNT(o) " +
            "FROM OpportunitiesEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND YEAR(o.createdDate) = :year AND o.opportunityStatus = 'Closed(won)' " +
            "GROUP BY MONTH(o.createdDate)")
    List<Object[]> getMonthlyOpportunitiesUser(
            @Param("year") int year,
            @Param("teamId") Team teamId);


    // calendar
    List<OpportunitiesEntity> findByEmployeeId(EmployeeEntity employeeId);

    // 직원 조회
    @Query("SELECT COUNT(o) FROM OpportunitiesEntity o WHERE o.employeeId.employeeId = :employeeId AND o.opportunityStatus IN ('Qualification', 'Needs Analysis', 'Proposal', 'Negotiation')")
    long countByEmployeeIdAndStatus(@Param("employeeId") String employeeId);

    @Query(value = "SELECT e.employee_name, COUNT(o.opportunity_id) AS opportunity_count " +
            "FROM opportunities o " +
            "JOIN employee e ON o.employee_id = e.employee_id " +
            "WHERE e.team_id = :team " +
            "GROUP BY e.employee_name " +
            "ORDER BY opportunity_count DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5ByTeamWithCount(@Param("team") String team);

    @Query(value = "SELECT e.employee_name, COUNT(o.opportunity_id) AS opportunity_count " +
            "FROM opportunities o " +
            "JOIN employee e ON o.employee_id = e.employee_id " +
            "WHERE e.department_id = :dept " +
            "GROUP BY e.employee_name " +
            "ORDER BY opportunity_count DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5ByDepartmentWithCount(@Param("dept") String dept);




}

