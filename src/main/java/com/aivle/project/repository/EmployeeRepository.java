package com.aivle.project.repository;

import com.aivle.project.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {
    boolean existsByEmployeeId(String username);
    EmployeeEntity findByEmployeeId(String username);
}
