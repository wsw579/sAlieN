package com.aivle.project.repository;

import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Spring-managed repository component
@Repository
public interface LeadsRepository extends JpaRepository<LeadsEntity, Long> {
    // retrieves a LeadsEntity by its leadId
    LeadsEntity findByLeadId(Long leadId);

    @Query("SELECT l.leadId, l.companyName FROM LeadsEntity l")
    List<Object[]> findAllLeadIdAndCompanyName();

    // 회사 이름으로 검색
    @Query("SELECT l FROM LeadsEntity l WHERE companyName LIKE %:companyName%")
    Page<LeadsEntity> findByCompanyNameLikeManager(@Param("companyName") String companyName, Pageable pageable);

    @Query("SELECT l FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND companyName LIKE %:companyName%")
    Page<LeadsEntity> findByCompanyNameLikeTeam(
            @Param("companyName") String companyName,
            @Param("teamId") Team teamId,
            Pageable pageable);

    @Query("SELECT l FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE e.teamId = :teamId")
    Page<LeadsEntity> findByTeamId(
            @Param("teamId") Team teamId,
            Pageable pageable);

    @Query("SELECT CAST(l.leadStatus AS string), COUNT(l) FROM LeadsEntity l GROUP BY l.leadStatus")
    List<Object[]> countLeadsByStatusManager();

    @Query("SELECT CAST(l.leadStatus AS string), COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +  // Employee와 JOIN
            "WHERE e.teamId = :teamId " + // employeeId 조건 추가
            "GROUP BY l.leadStatus")
    List<Object[]> countLeadsByStatusTeam(@Param("teamId") Team teamId);

    // 차트 그래프
    @Query("SELECT MONTH(l.targetCloseDate), COUNT(l) " +
            "FROM LeadsEntity l " +
            "WHERE YEAR(l.targetCloseDate) = :year AND l.leadStatus = 'Accepted' " +
            "GROUP BY MONTH(l.targetCloseDate)")
    List<Object[]> getMonthlyLeadsManager(@Param("year") int year);

    @Query("SELECT MONTH(l.targetCloseDate), COUNT(l) " +
            "FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND YEAR(l.targetCloseDate) = :year AND l.leadStatus = 'Accepted' " +
            "GROUP BY MONTH(l.targetCloseDate)")
    List<Object[]> getMonthlyLeadsTeam(
            @Param("year") int year,
            @Param("teamId") Team teamId);

    // createdDate가 오늘인 데이터의 개수를 반환 - 개인
    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.createdDate = :today " +
            "AND e.employeeId = :employeeId")
    long countTodayLeadsUser(@Param("today") LocalDate today,
                                @Param("employeeId") String employeeId);

    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.leadStatus = :leadStatus " +
            "AND e.employeeId = :employeeId")
    long countLeadsByStatusUser(@Param("leadStatus") String leadStatus,
                                          @Param("employeeId") String employeeId);

    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.targetCloseDate = :targetCloseDate " +
            "AND e.employeeId = :employeeId")
    long countLeadsWithTargetCloseDateTodayUser(@Param("targetCloseDate") LocalDate targetCloseDate,
                                  @Param("employeeId") String employeeId);

}
