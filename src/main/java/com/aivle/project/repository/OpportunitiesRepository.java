package com.aivle.project.repository;

import com.aivle.project.entity.OpportunitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OpportunitiesRepository extends JpaRepository<OpportunitiesEntity, Long> {

    OpportunitiesEntity findByOpportunityId(Long opportunityId);

}
