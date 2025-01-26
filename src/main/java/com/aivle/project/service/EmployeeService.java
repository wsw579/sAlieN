package com.aivle.project.service;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.OpportunitiesRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OpportunitiesRepository opportunitiesRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    private static final Map<Position, Float> positionBaseSalaryMap = new HashMap<>();
    private static final Map<Dept, String> deptMap = new HashMap<>();
    private static final Map<Position, String> positionMap = new HashMap<>();
    private static final Map<Team, String> teamMap = new HashMap<>();

    static {
        // static 블록에서 초기화
        positionBaseSalaryMap.put(Position.STAFF, 50000000F);
        positionBaseSalaryMap.put(Position.JUNIOR, 60000000F);
        positionBaseSalaryMap.put(Position.ASSOCIATE, 75000000F);
        positionBaseSalaryMap.put(Position.MANAGER, 100000000F);
        positionBaseSalaryMap.put(Position.ASSISTANT_MANAGER, 120000000F);
        positionBaseSalaryMap.put(Position.TEAM_LEADER, 140000000F);
        positionBaseSalaryMap.put(Position.DEPARTMENT_HEAD, 160000000F);
        positionBaseSalaryMap.put(Position.GENERAL_MANAGER, 180000000F);
    }

    static {
        deptMap.put(Dept.STRATEGY_DEPT, "전략고객본부");
        deptMap.put(Dept.PUBLIC_DEPT, "공공고객본부");
        deptMap.put(Dept.FINANCE_DEPT, "금융고객본부");
        deptMap.put(Dept.CORPORATE_DEPT, "법인영업본부");
    }

    static {
        // Position 매핑 초기화
        positionMap.put(Position.GENERAL_MANAGER, "본부장");
        positionMap.put(Position.DEPARTMENT_HEAD, "부장");
        positionMap.put(Position.TEAM_LEADER, "팀장");
        positionMap.put(Position.ASSISTANT_MANAGER, "차장");
        positionMap.put(Position.MANAGER, "과장");
        positionMap.put(Position.ASSOCIATE, "대리");
        positionMap.put(Position.JUNIOR, "주임");
        positionMap.put(Position.STAFF, "사원");
    }

    static {
        // Team 매핑 초기화
        teamMap.put(Team.STRATEGY_CUST_SECTOR, "전략고객섹터담당");
        teamMap.put(Team.STRATEGY_CUST_1, "전략고객1담당");
        teamMap.put(Team.STRATEGY_CUST_2, "전략고객2담당");
        teamMap.put(Team.PUBLIC_CUST_SECTOR, "공공고객섹터담당");
        teamMap.put(Team.PUBLIC_CUST_1, "공공고객1담당");
        teamMap.put(Team.PUBLIC_CUST_2, "공공고객2담당");
        teamMap.put(Team.FINANCE_CUST_SECTOR, "금융고객섹터담당");
        teamMap.put(Team.FINANCE_CUST_1, "금융고객1담당");
        teamMap.put(Team.FINANCE_CUST_2, "금융고객2담당");
        teamMap.put(Team.CORPORATE_SALES_PLANNING, "법인영업기획담당");
        teamMap.put(Team.CORPORATE_CUST, "법인고객담당");
        teamMap.put(Team.CORPORATE_RETAIL, "법인유통담당");
        teamMap.put(Team.CORPORATE_SALES_SECTOR, "법인섹터담당");
    }

    public void join(EmployeeDto.Post employeeDto) {
        // EmployeeEntity 생성 및 저장
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId(employeeDto.getEmployeeId());
        employee.setEmployeeName(employeeDto.getEmployeeName());
        employee.setHireDate(LocalDate.parse(employeeDto.getHireDate()));
        employee.setTerminationDate(null);
        employee.setBaseSalary(positionBaseSalaryMap.get(Position.valueOf(employeeDto.getPosition())));
        employee.setPosition(Position.valueOf(employeeDto.getPosition()));
        employee.setAccessPermission(Role.ROLE_USER);
        employee.setPassword(bCryptPasswordEncoder.encode(employeeDto.getPassword()));
        employee.setPasswordAnswer(employeeDto.getPasswordAnswer());
        employee.setDepartmentId(Dept.valueOf(employeeDto.getDept()));
        employee.setTeamId(Team.valueOf(employeeDto.getTeam()));
        employeeRepository.save(employee);
    }

    public String editPassword(EmployeeDto.Patch employee) {
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());
        if (findEmployee == null) {
            throw new IllegalArgumentException("사원을 찾을 수 없습니다.");
        }

        if (!bCryptPasswordEncoder.matches(employee.getExistPassword(), findEmployee.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호가 일치하면 새 비밀번호 저장
        findEmployee.setPassword(bCryptPasswordEncoder.encode(employee.getNewPassword()));
        findEmployee.setPasswordAnswer(employee.getPasswordAnswer());
        employeeRepository.save(findEmployee);
        return findEmployee.getEmployeeId();
    }

    public String findPassword(EmployeeDto.Patch employee) {
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());
        if (findEmployee == null) {
            throw new IllegalArgumentException("사원을 찾을 수 없습니다.");
        }

        if (!bCryptPasswordEncoder.matches(employee.getExistPassword(), findEmployee.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호가 일치하면 새 비밀번호 저장
        findEmployee.setPassword(bCryptPasswordEncoder.encode(employee.getNewPassword()));
        employeeRepository.save(findEmployee);
        return findEmployee.getEmployeeId();
    }


    public EmployeeDto.Get findEmployeeById(String employeeId) {
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employeeId);
        EmployeeDto.Get employee = new EmployeeDto.Get();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(findEmployee.getEmployeeName());
        employee.setHireDate(findEmployee.getHireDate());
        employee.setPosition(positionMap.get(findEmployee.getPosition()));
        employee.setTeam(findEmployee.getTeamId() == null ? "팀 정보 없음" : teamMap.get(findEmployee.getTeamId()));
        employee.setDept(findEmployee.getDepartmentId() == null ? "부서정보 없음" : deptMap.get(findEmployee.getDepartmentId()));
        return employee;
    }

    public void loadEmployeeDataFromCSV(String csvFilePath) throws IOException {
        // CSV 파일 읽기
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            reader.readNext(); // 첫 번째 줄은 헤더이므로 건너뜀
            while ((line = reader.readNext()) != null) {
                EmployeeEntity employee = new EmployeeEntity();
                employee.setEmployeeId(line[0]);
                employee.setEmployeeName(line[1]);
                employee.setHireDate(LocalDate.parse(line[2], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                employee.setTerminationDate(line[3].isEmpty() ? null : LocalDate.parse(line[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                employee.setBaseSalary(Float.parseFloat(line[4]));
                employee.setPosition(Position.valueOf(line[5]));
                employee.setAccessPermission(Role.valueOf(line[6]));
                employee.setPassword(bCryptPasswordEncoder.encode(line[7]));
                employee.setPasswordAnswer(line[8]);
                employee.setDepartmentId(line[9].isEmpty() ? null : Dept.valueOf(line[9]));
                employee.setTeamId(line[10].isEmpty() ? null : Team.valueOf(line[10]));
                employeeRepository.save(employee);  // 데이터베이스에 저장
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public String makeNewEmployeeId(String year) {
        // 데이터베이스에서 해당 연도에 가장 마지막으로 등록된 employeeId 조회
        String lastEmployeeId = employeeRepository.findLastEmployeeIdByYear(year);

        // 해당 연도의 마지막 번호 계산
        int nextNumber = 1; // 기본값은 1 (입사자가 없을 경우)
        if (lastEmployeeId != null && lastEmployeeId.startsWith(year)) {
            // 연도 이후의 번호를 추출하고 숫자로 변환하여 증가
            nextNumber = Integer.parseInt(lastEmployeeId.substring(4)) + 1;
        }
        return String.format("%s%04d", year, nextNumber);
    }

    public List<EmployeeDto.Get> findAllEmployee() {
        List<EmployeeEntity> empList = employeeRepository.findAllByAccessPermission(Role.ROLE_USER);
        List<EmployeeDto.Get> empDtoList = new ArrayList<>();
        for (EmployeeEntity emp : empList) {
            EmployeeDto.Get empDto = new EmployeeDto.Get();
            empDto.setEmployeeId(emp.getEmployeeId());
            empDto.setEmployeeName(emp.getEmployeeName());
            empDto.setHireDate(emp.getHireDate());
            empDto.setPosition(positionMap.get(emp.getPosition()));
            empDto.setDept(emp.getDepartmentId() == null ? "부서 정보 없음" : deptMap.get(emp.getDepartmentId()));
            empDto.setTeam(emp.getTeamId() == null ? "팀 정보 없음" : teamMap.get(emp.getTeamId()));
            empDto.setBaseSalary(emp.getBaseSalary());
            empDtoList.add(empDto);
        }
        return empDtoList;
    }

    public boolean resetEmployeePassword(String employeeId) {
        try {
            // Employee 엔티티를 데이터베이스에서 조회
            EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employeeId);
            if (findEmployee == null) {
                return false; // 사원이 존재하지 않을 경우 false 반환
            }

            // 비밀번호를 "1234"로 초기화
            findEmployee.setPassword(bCryptPasswordEncoder.encode("1234"));

            // 변경사항 저장
            employeeRepository.save(findEmployee);

            return true; // 성공적으로 초기화 시 true 반환
        } catch (Exception e) {
            // 오류 발생 시 false 반환
            e.printStackTrace(); // 예외 로그 출력
            return false;
        }
    }

    public void deleteByIds(List<String> ids) {
        employeeRepository.deleteAllById(ids);
    }

    // detail select를 위한 이름 id 불러오기
    public List<EmployeeDto.GetId> getAllEmployeeIdsAndNamesAndDepartmentIds() {
        List<Object[]> results = employeeRepository.findAllEmployeeIdAndEmployeeNameAndDepartmentId();
        List<EmployeeDto.GetId> dtoList = results.stream()
                .map(result -> {
                    EmployeeDto.GetId dto = new EmployeeDto.GetId();
                    dto.setEmployeeId((String) result[0]);
                    dto.setEmployeeName((String) result[1]);
                    if (result[2] != null) {
                        dto.setDepartmentId((Dept) result[2]);
                    } else {
                        System.out.println("Department is null");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
                    return dtoList;
    }

    public String getPositionByUserId(String userId) {
        return employeeRepository.findPositionById(userId);
    }

    public List<Map<String, Object>> getSalesDataByTeam(String teamId) {
        List<EmployeeEntity> employees = employeeRepository.findByTeamId(teamId);
        return employees.stream()
                .map(employee -> {
                    long opportunityCount = opportunitiesRepository.countByEmployeeIdAndStatus(employee.getEmployeeId());
                    Map<String, Object> data = new HashMap<>();
                    data.put("employeeName", employee.getEmployeeName());
                    data.put("opportunityCount", opportunityCount);
                    return data;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSalesDataByDepartment(String departmentId) {
        List<EmployeeEntity> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream()
                .map(employee -> {
                    long opportunityCount = opportunitiesRepository.countByEmployeeIdAndStatus(employee.getEmployeeId());
                    Map<String, Object> data = new HashMap<>();
                    data.put("employeeName", employee.getEmployeeName());
                    data.put("opportunityCount", opportunityCount);
                    return data;
                })
                .collect(Collectors.toList());
    }


    // Object[] 결과를 Map으로 변환
    private List<Map<String, Object>> mapToSalesData(List<Object[]> results) {
        return results.stream().map(result -> {
            Map<String, Object> data = new HashMap<>();
            data.put("employeeName", result[0]);
            data.put("opportunityCount", result[1]);
            return data;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getSalesData(Team team, Dept dept) {
        List<OpportunitiesEntity> opportunities;

        if (team != null) {
            opportunities = opportunitiesRepository.findByTeam(team);
        } else if (dept != null) {
            opportunities = opportunitiesRepository.findByDepartment(dept);
        } else {
            throw new IllegalArgumentException("팀 또는 부서가 필요합니다.");
        }

        // 데이터를 그룹화하여 이름별로 기회 수를 합산
        Map<String, Long> groupedData = opportunities.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getEmployeeId().getEmployeeName(), // 직원 이름을 키로
                        Collectors.counting() // 각 직원 이름에 해당하는 기회 수를 합산
                ));

        // 응답 데이터 변환
        Map<String, Object> response = new HashMap<>();
        response.put("labels", new ArrayList<>(groupedData.keySet())); // 직원 이름 리스트
        response.put("values", new ArrayList<>(groupedData.values())); // 각 직원의 기회 수 리스트

        return response;
    }

    public EmployeeDto.Get getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("현재 로그인된 사용자가 없습니다.");
        }

        String loggedInEmployeeId = authentication.getName(); // 로그인된 사용자의 ID 가져오기
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(loggedInEmployeeId);

        if (findEmployee == null) {
            throw new IllegalArgumentException("로그인된 사용자의 정보를 찾을 수 없습니다.");
        }

        EmployeeDto.Get employee = new EmployeeDto.Get();
        employee.setEmployeeId(findEmployee.getEmployeeId());
        employee.setEmployeeName(findEmployee.getEmployeeName());
        employee.setHireDate(findEmployee.getHireDate());
        employee.setTerminationDate(findEmployee.getTerminationDate());
        employee.setBaseSalary(findEmployee.getBaseSalary());
        employee.setPosition(findEmployee.getPosition().name());
        employee.setAccessPermission(findEmployee.getAccessPermission().name());
        employee.setDept(findEmployee.getDepartmentId() != null ? findEmployee.getDepartmentId().toString() : null);
        employee.setTeam(findEmployee.getTeamId() != null ? findEmployee.getTeamId().toString() : null);

        return employee;
    }
}
