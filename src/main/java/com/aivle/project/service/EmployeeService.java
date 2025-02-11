package com.aivle.project.service;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.utils.EmployeeDataMapping;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
        employee.setPosition(EmployeeDataMapping.POSITION_MAP.get(findEmployee.getPosition()));
        employee.setTeam(findEmployee.getTeamId() == null ? "팀 정보 없음" : EmployeeDataMapping.TEAM_MAP.get(findEmployee.getTeamId()));
        employee.setDept(findEmployee.getDepartmentId() == null ? "부서정보 없음" : EmployeeDataMapping.DEPT_MAP.get(findEmployee.getDepartmentId()));
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

    public Page<EmployeeDto.Get> findAllEmployee(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size); // 페이징 정보 설정

        // ✅ 직급순 정렬을 포함한 쿼리 실행
        Page<EmployeeEntity> empPage = (search == null || search.isBlank())
                ? employeeRepository.findAllByAccessPermission(Role.ROLE_USER, pageable)
                : employeeRepository.findAllByAccessPermissionAndNameLike(Role.ROLE_USER, search, pageable);

        // ✅ DTO 변환 (정렬된 상태 그대로 유지)
        List<EmployeeDto.Get> employeeDtos = empPage.getContent().stream()
                .map(emp -> {
                    EmployeeDto.Get empDto = new EmployeeDto.Get();
                    empDto.setEmployeeId(emp.getEmployeeId());
                    empDto.setEmployeeName(emp.getEmployeeName());
                    empDto.setHireDate(emp.getHireDate());
                    empDto.setPosition(emp.getPosition().name());
                    empDto.setDept(emp.getDepartmentId() == null ? "부서 정보 없음" : EmployeeDataMapping.DEPT_MAP.get(emp.getDepartmentId()));
                    empDto.setTeam(emp.getTeamId() == null ? "팀 정보 없음" : EmployeeDataMapping.TEAM_MAP.get(emp.getTeamId()));
                    empDto.setBaseSalary(emp.getBaseSalary());
                    return empDto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(employeeDtos, pageable, empPage.getTotalElements()); // 정렬된 데이터 반환
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
