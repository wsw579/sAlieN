package com.aivle.project.service;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.enums.Team;
import com.aivle.project.repository.EmployeeRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(EmployeeDto.Post memberDto){

        // 동일한 id를 가진 user가 존재하는지 검증
        boolean findUser = employeeRepository.existsByEmployeeId(memberDto.getEmployeeId());
        if(findUser){
            System.out.println("중복된 사용자 입니다.");
            return;
        }

        EmployeeEntity member = new EmployeeEntity();
        member.setEmployeeId(memberDto.getEmployeeId());
        member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        member.setEmployeeName(memberDto.getEmployeeName());
        member.setHireDate(LocalDate.parse(memberDto.getHireDate()));
        member.setPosition(Position.valueOf(memberDto.getPosition()));
        member.setAccessPermission(Role.ROLE_USER);
        member.setPasswordAnswer(memberDto.getPasswordAnswer());
        employeeRepository.save(member);
    }

    public String editPassword(EmployeeDto.Patch employee) {
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());

        // 평문 비밀번호를 암호화된 비밀번호와 비교
        if (bCryptPasswordEncoder.matches(employee.getExistPassword(), findEmployee.getPassword())) {
            // 비밀번호 일치 - 새 비밀번호 저장
            findEmployee.setPassword(bCryptPasswordEncoder.encode(employee.getNewPassword())); // 새 비밀번호 암호화하여 저장
            employeeRepository.save(findEmployee);
            return findEmployee.getEmployeeId();
        } else {
            // 비밀번호 불일치
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
    }

    public EmployeeDto.Get findEmployeeById(String employeeId) {
        EmployeeEntity findEmployee = employeeRepository.findByEmployeeId(employeeId);
        EmployeeDto.Get employee = new EmployeeDto.Get();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(findEmployee.getEmployeeName());
        employee.setHireDate(findEmployee.getHireDate());
        employee.setPosition(findEmployee.getPosition());
        employee.setTeam("영업1팀");
        employee.setDept("영업부");
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

}
