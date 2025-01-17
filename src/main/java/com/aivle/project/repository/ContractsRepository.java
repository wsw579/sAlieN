package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractsRepository extends JpaRepository<ContractsEntity, Long> {
    ContractsEntity findByContractId(Long contractId);

    @Query("SELECT o FROM ContractsEntity o ORDER BY o.startDate DESC, o.contractId DESC")
    List<ContractsEntity> findAllByOrderByCreatedDateAndIdDescActive();

}
