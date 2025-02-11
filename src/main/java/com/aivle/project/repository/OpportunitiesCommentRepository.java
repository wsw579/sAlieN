package com.aivle.project.repository;

import com.aivle.project.entity.OpportunitiesCommentEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OpportunitiesCommentRepository extends JpaRepository<OpportunitiesCommentEntity, Long> {
    List<OpportunitiesCommentEntity> findByOpportunity(OpportunitiesEntity opportunity);
}
