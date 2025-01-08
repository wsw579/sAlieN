package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractsRepository extends JpaRepository<ContractsEntity, Long> {
    ContractsEntity findByContractId(Long contractId);

    @Query("SELECT o FROM ContractsEntity o ORDER BY o.startDate DESC, o.contractId DESC")
    List<ContractsEntity> findAllByOrderByCreatedDateAndIdDesc();
}
