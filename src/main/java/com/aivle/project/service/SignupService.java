package com.aivle.project.service;

import com.aivle.project.Validator.PasswordValidator;
import com.aivle.project.utils.EmployeeDataMapping;
import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class SignupService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(EmployeeDto.Post employeeDto) {
        // EmployeeEntity 생성 및 저장
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId(employeeDto.getEmployeeId());
        employee.setEmployeeName(employeeDto.getEmployeeName());
        employee.setHireDate(LocalDate.parse(employeeDto.getHireDate()));
        employee.setTerminationDate(null);
        employee.setBaseSalary(EmployeeDataMapping.POSITION_BASE_SALARY_MAP.get(Position.valueOf(employeeDto.getPosition())));
        employee.setPosition(Position.valueOf(employeeDto.getPosition()));
        employee.setAccessPermission(Role.ROLE_USER);

        // 비밀번호 해싱 후 저장
        employee.setPassword(bCryptPasswordEncoder.encode(employeeDto.getPassword()));
        employee.setPasswordAnswer(employeeDto.getPasswordAnswer());
        employee.setDepartmentId(Dept.valueOf(employeeDto.getDept()));
        employee.setTeamId(Team.valueOf(employeeDto.getTeam()));

        // 저장
        employeeRepository.save(employee);
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
}
