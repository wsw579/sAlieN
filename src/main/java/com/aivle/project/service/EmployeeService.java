package com.aivle.project.service;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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


//    public MemberDto.Get findByEmployeeId(String employeeId) {
//        MemberEntity findMember = memberRepository.findByEmployeeId(employeeId);
//        MemberDto.Get member = new MemberDto.Get();
//
//
//    }
}
