package com.aivle.project.repository;

import com.aivle.project.dto.OrdersDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    List<OrdersEntity> findByContractId(ContractsEntity contractId);

    @Query("SELECT o FROM OrdersEntity o " +
            "JOIN o.productId p " +
            "WHERE p.productName LIKE %:productName%")
    Page<OrdersEntity> findByOrderIdLikeManager(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT o FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "JOIN o.productId p " +
            "WHERE e.teamId = :teamId " +
            "AND p.productName LIKE %:productName%")
    Page<OrdersEntity> findByOrderIdLikeUser(
            @Param("productName") String productName,
            @Param("teamId") Team teamId,
            Pageable pageable);

    @Query("SELECT o FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId ")
    Page<OrdersEntity> findByTeamId(
            @Param("teamId") Team teamId,
            Pageable pageable);

    // 일반 사원을 위한 팀의 상태 수 세기
    @Query("SELECT CAST(o.orderStatus AS string), COUNT(o) " +
            "FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "GROUP BY o.orderStatus")
    List<Object[]> countOrdersByStatusForCurrentUser(@Param("teamId") Team teamId);

    // 관리자들을 위한 전체 상태의 수 세기
    @Query("SELECT CAST(o.orderStatus AS string), COUNT(o) " +
            "FROM OrdersEntity o " +
            "GROUP BY o.orderStatus")
    List<Object[]> countOrdersByStatusForCurrentManager();


    // 차트 그래프 - 일반 사원
    @Query("SELECT MONTH(o.orderDate), COUNT(o) " +
            "FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "WHERE YEAR(o.orderDate) = :year AND o.orderStatus = 'activated' " +
            "AND e.teamId = :teamId " +
            "GROUP BY MONTH(o.orderDate)")
    List<Object[]> getMonthlyOrdersUser(@Param("year") int year,
                                        @Param("teamId") Team teamId);

    // 차트 그래프 - 관리자
    @Query("SELECT MONTH(o.orderDate), COUNT(o) " +
            "FROM OrdersEntity o " +
            "WHERE YEAR(o.orderDate) = :year AND o.orderStatus = 'activated' " +
            "GROUP BY MONTH(o.orderDate)")
    List<Object[]> getMonthlyOrdersManager(@Param("year") int year);




    // 메인화면
    // 영업 실적 그래프
    @Query("SELECT e.employeeId AS employeeId, e.employeeName AS employeeName, " +
            "SUM(o.orderAmount * p.fixedPrice) AS totalSales " +
            "FROM OrdersEntity o " +
            "JOIN o.productId p " +
            "JOIN o.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND MONTH(o.salesDate) = MONTH(CURRENT_DATE) " + // 같은 달 조건
            "AND YEAR(o.salesDate) = YEAR(CURRENT_DATE) " +   // 같은 연도 조건
            "GROUP BY e.employeeId, e.employeeName " +
            "ORDER BY totalSales DESC")
    List<Object[]> getSalesByEmployeeWithNames(@Param("teamId") Team teamId);

    // 이번달 주문 현황 퍼센트 표시
    @Query("SELECT COUNT(o) FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.employeeId = :employeeId " +
            "AND EXTRACT(MONTH FROM o.salesDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM o.salesDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND o.orderStatus IN ('draft', 'completed', 'activated')")
    long countTotalSalesThisMonth(@Param("employeeId") String employeeId);

    @Query("SELECT COUNT(o) FROM OrdersEntity o " +
            "JOIN o.employeeId e " +
            "WHERE e.employeeId = :employeeId " +
            "AND EXTRACT(MONTH FROM o.salesDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM o.salesDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND o.orderStatus = 'draft'")
    long countDraftSalesThisMonth(@Param("employeeId") String employeeId);

    // 연도별 팀 매출 합
    @Query(value = "SELECT MONTH(o.salesDate) AS month, " +
            "SUM(o.orderAmount * p.fixedPrice) AS revenue " +
            "FROM OrdersEntity o " +
            "JOIN o.productId p " +
            "JOIN o.employeeId e " +
            "WHERE YEAR(o.salesDate) = :year " +
            "AND o.orderStatus = 'activated' " +
            "AND e.teamId = :teamId " +
            "GROUP BY MONTH(o.salesDate)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year,
                                     @Param("teamId") Team teamId);

    //관리자 메인 매출
    @Query(value = "SELECT MIN(EXTRACT(YEAR FROM o.order_date)) AS minYear, " +
            "MAX(EXTRACT(YEAR FROM o.order_date)) AS maxYear " +
            "FROM orders o", nativeQuery = true)
    Object[] findMinAndMaxYears();


    @Query(value = "SELECT " +
            "TO_CHAR(o.order_date, 'YYYY-MM') AS month, " +
            "SUM(o.order_amount * p.dealer_price) AS totalRevenue, " +
            "SUM(o.order_amount * p.cost_price) AS totalPurchase " +
            "FROM orders o " +
            "JOIN products p ON o.product_id = p.product_id " +
            "JOIN employee e ON o.employee_id = e.employee_id " +
            "WHERE (:team IS NULL OR e.team_id = :team) " +
            "AND (:department IS NULL OR e.department_id = :department) " +
            "AND EXTRACT(YEAR FROM o.order_date) = :year " +
            "GROUP BY TO_CHAR(o.order_date, 'YYYY-MM') " +
            "ORDER BY month", nativeQuery = true)
    List<Object[]> findMonthlyRevenueAndPurchaseByTeamAndDepartment(
            @Param("team") String team,
            @Param("department") String department,
            @Param("year") int year
    );

    //대시보드 order 현황
    @Query("SELECT e.employeeName AS employeeName, COUNT(o.orderId) AS totalOrders, SUM(CASE WHEN LOWER(o.orderStatus) = 'completed' THEN 1 ELSE 0 END) AS completedOrders " +
            "FROM OrdersEntity o JOIN o.employeeId e " +
            "WHERE e.teamId = :team AND o.salesDate BETWEEN :startDate AND :endDate AND LOWER(o.orderStatus) IN ('activated', 'completed') " +
            "GROUP BY e.employeeName")
    List<Object[]> findTeamOrdersGroupedByEmployee(@Param("team") Team team, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.employeeName AS employeeName, COUNT(o.orderId) AS totalOrders, SUM(CASE WHEN LOWER(o.orderStatus) = 'completed' THEN 1 ELSE 0 END) AS completedOrders " +
            "FROM OrdersEntity o JOIN o.employeeId e " +
            "WHERE e.departmentId = :department AND o.salesDate BETWEEN :startDate AND :endDate AND LOWER(o.orderStatus) IN ('activated', 'completed') " +
            "GROUP BY e.employeeName")
    List<Object[]> findDepartmentOrdersGroupedByEmployee(@Param("department") Dept department, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}