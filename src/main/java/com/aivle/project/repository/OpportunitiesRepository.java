package com.aivle.project.repository;

import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


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



    // 차트 그래프
    @Query("SELECT MONTH(o.createdDate), COUNT(o) " +
            "FROM OpportunitiesEntity o " +
            "WHERE YEAR(o.createdDate) = :year AND o.opportunityStatus = 'Closed(won)' " +
            "GROUP BY MONTH(o.createdDate)")
    List<Object[]> getMonthlyOpportunities(@Param("year") int year);
}
