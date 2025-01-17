package com.aivle.project.repository;
import com.aivle.project.entity.AdditionalOpportunitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AdditionalOpportunitiesRepository extends JpaRepository<AdditionalOpportunitiesEntity, Long> {
    AdditionalOpportunitiesEntity findByAdditionalopportunityId(Long additionalopportunityId);
    @Query("SELECT ao FROM AdditionalOpportunitiesEntity ao ORDER BY ao.createdDate DESC, ao.additionalopportunityId DESC")
    List<AdditionalOpportunitiesEntity> findAllByOrderByCreatedDateAndIdDesc();
}
