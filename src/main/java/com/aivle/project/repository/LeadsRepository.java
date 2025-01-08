package com.aivle.project.repository;

import com.aivle.project.entity.LeadsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Spring-managed repository component
@Repository
public interface LeadsRepository extends JpaRepository<LeadsEntity, Long> {
    // retrieves a LeadsEntity by its leadId
    LeadsEntity findByLeadId(Long leadId);


    // annotation used to define a custom query
    @Query("SELECT l FROM LeadsEntity l ORDER BY l.createdDate DESC, l.leadId DESC")
    // query method :
    List<LeadsEntity> findAllByOrderByCreatedDateAndIdDesc();


}
