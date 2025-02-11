package com.aivle.project.repository;

import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    @Query("SELECT o FROM HistoryEntity o WHERE o.opportunity = :opportunity ORDER BY o.historyDate DESC, o.historyTime DESC")
    List<HistoryEntity> findByOpportunityOrderByDateTimeDesc(@Param("opportunity") OpportunitiesEntity opportunity);

    List<HistoryEntity> findByOpportunity(OpportunitiesEntity opportunity);
}