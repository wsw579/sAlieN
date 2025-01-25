package com.aivle.project.repository;

import com.aivle.project.entity.LeadsEntity;
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

    @Query("SELECT l FROM LeadsEntity l WHERE CAST(l.leadId AS string) LIKE %:leadId%")
    Page<LeadsEntity> findByLeadIdLike(@Param("leadId") String leadId, Pageable pageable);

    @Query("SELECT CAST(l.leadStatus AS string), COUNT(l) FROM LeadsEntity l GROUP BY l.leadStatus")
    List<Object[]> countLeadsByStatus();

    // 차트 그래프
    @Query("SELECT MONTH(l.targetCloseDate), COUNT(l) " +
            "FROM LeadsEntity l " +
            "WHERE YEAR(l.targetCloseDate) = :year AND l.leadStatus = 'Accepted' " +
            "GROUP BY MONTH(l.targetCloseDate)")
    List<Object[]> getMonthlyLeads(@Param("year") int year);

    // createdDate가 오늘인 데이터의 개수를 반환
    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.createdDate = :today " +
            "AND e.teamId = :teamId")
    long countTodayLeadsForTeam(@Param("today") LocalDate today,
                                @Param("teamId") Team teamId);

    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.leadStatus = :leadStatus " +
            "AND e.teamId = :teamId")
    long countLeadsByStatusForTeam(@Param("leadStatus") String leadStatus,
                                          @Param("teamId") Team teamId);

    @Query("SELECT COUNT(l) FROM LeadsEntity l " +
            "JOIN l.employeeId e " +
            "WHERE l.targetCloseDate = :targetCloseDate " +
            "AND e.teamId = :teamId")
    long countLeadsWithTargetCloseDateTodayForTeam(@Param("targetCloseDate") LocalDate targetCloseDate,
                                  @Param("teamId") Team teamId);

}
