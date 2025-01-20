package com.aivle.project.repository;

import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OrdersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Spring-managed repository component
@Repository
public interface LeadsRepository extends JpaRepository<LeadsEntity, Long> {
    // retrieves a LeadsEntity by its leadId
    LeadsEntity findByLeadId(Long leadId);

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
}
