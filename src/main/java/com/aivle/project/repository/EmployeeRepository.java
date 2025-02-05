package com.aivle.project.repository;

import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT e FROM EmployeeEntity e WHERE e.accessPermission = :role ORDER BY " +
            "CASE " +
            "WHEN e.position = 'GENERAL_MANAGER' THEN 1 " +
            "WHEN e.position = 'DEPARTMENT_HEAD' THEN 2 " +
            "WHEN e.position = 'TEAM_LEADER' THEN 3 " +
            "WHEN e.position = 'ASSISTANT_MANAGER' THEN 4 " +
            "WHEN e.position = 'MANAGER' THEN 5 " +
            "WHEN e.position = 'ASSOCIATE' THEN 6 " +
            "WHEN e.position = 'JUNIOR' THEN 7 " +
            "WHEN e.position = 'STAFF' THEN 8 " +
            "ELSE 9 " +
            "END, e.employeeName ASC",
            countQuery = "SELECT COUNT(e) FROM EmployeeEntity e WHERE e.accessPermission = :role")
    Page<EmployeeEntity> findAllByAccessPermission(@Param("role") Role role, Pageable pageable);

    // 이름으로 검색
    // order by 사용시 자동 페이징이 불가능 -> count 이용
    @Query(value = "SELECT e FROM EmployeeEntity e WHERE e.accessPermission = :role " +
            "AND e.employeeName LIKE CONCAT('%', :search, '%') " + // ✅ 이름 검색 조건 수정
            "ORDER BY " +
            "CASE " +
            "WHEN e.position = 'GENERAL_MANAGER' THEN 1 " +
            "WHEN e.position = 'DEPARTMENT_HEAD' THEN 2 " +
            "WHEN e.position = 'TEAM_LEADER' THEN 3 " +
            "WHEN e.position = 'ASSISTANT_MANAGER' THEN 4 " +
            "WHEN e.position = 'MANAGER' THEN 5 " +
            "WHEN e.position = 'ASSOCIATE' THEN 6 " +
            "WHEN e.position = 'JUNIOR' THEN 7 " +
            "WHEN e.position = 'STAFF' THEN 8 " +
            "ELSE 9 " +
            "END, e.employeeName ASC",
            countQuery = "SELECT COUNT(e) FROM EmployeeEntity e WHERE e.accessPermission = :role " + // ✅ 검색 조건 추가
                    "AND e.employeeName LIKE CONCAT('%', :search, '%')")
    Page<EmployeeEntity> findAllByAccessPermissionAndNameLike(@Param("role") Role role, @Param("search") String search, Pageable pageable);



    @Query("SELECT e FROM EmployeeEntity e WHERE e.teamId = :teamId")
    List<EmployeeEntity> findByTeamId(@Param("teamId") String teamId);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.departmentId = :departmentId")
    List<EmployeeEntity> findByDepartmentId(@Param("departmentId") String departmentId);
}
