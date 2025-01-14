package com.aivle.project.repository;

import com.aivle.project.entity.OpportunitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OpportunitiesRepository extends JpaRepository<OpportunitiesEntity, Long> {

    OpportunitiesEntity findByOpportunityId(Long opportunityId);

    @Query("SELECT o FROM OpportunitiesEntity o ORDER BY o.createdDate DESC, o.opportunityId DESC")
    List<OpportunitiesEntity> findAllByOrderByCreatedDateAndIdDesc();

}
