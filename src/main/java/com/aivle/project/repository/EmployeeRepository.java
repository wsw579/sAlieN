package com.aivle.project.repository;

import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {
    boolean existsByEmployeeId(String username);
    EmployeeEntity findByEmployeeId(String username);
    boolean existsByAccessPermission(Role role);

    @Query("SELECT e.departmentId FROM EmployeeEntity e WHERE e.employeeId = :employeeId")
    String findDeptById(@Param("employeeId") String employeeId);

    @Query("SELECT e.teamId FROM EmployeeEntity e WHERE e.employeeId = :employeeId")
    String findTeamById(@Param("employeeId") String employeeId);

    @Query("SELECT e.position FROM EmployeeEntity e WHERE e.employeeId = :employeeId")
    String findPositionById(@Param("employeeId") String employeeId);

    @Query("SELECT e.employeeId, e.employeeName, e.departmentId FROM EmployeeEntity e")
    List<Object[]> findAllEmployeeIdAndEmployeeNameAndDepartmentId();

    @Query("SELECT e.employeeId FROM EmployeeEntity e WHERE e.employeeId LIKE CONCAT(:year, '%') ORDER BY e.employeeId DESC LIMIT 1")
    String findLastEmployeeIdByYear(@Param("year") String year);

    List<EmployeeEntity> findAllByAccessPermission(Role role);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.teamId = :teamId")
    List<EmployeeEntity> findByTeamId(@Param("teamId") String teamId);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.departmentId = :departmentId")
    List<EmployeeEntity> findByDepartmentId(@Param("departmentId") String departmentId);
}
