package com.aivle.project.config;

import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AccountInitializer implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(!employeeRepository.existsByAccessPermission(Role.ROLE_ADMIN)) {
            EmployeeEntity admin = new EmployeeEntity();
            admin.setAccessPermission(Role.ROLE_ADMIN);
            admin.setEmployeeName("admin");
            admin.setEmployeeId("admin");
            admin.setPassword(bCryptPasswordEncoder.encode("1234"));
            admin.setHireDate(LocalDate.now());
            admin.setBaseSalary(1000f);
            admin.setPosition(Position.DEPARTMENT_HEAD);
            admin.setPasswordAnswer("admin");
            employeeRepository.save(admin);
        }

        if(!employeeRepository.existsByAccessPermission(Role.ROLE_USER)) {
            // CSV 파일 경로 지정
            employeeService.loadEmployeeDataFromCSV("src/main/resources/static/data/sample/employee_data.csv");


        }

    }
}
