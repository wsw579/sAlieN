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
        boolean findUser = employeeRepository.existsByEmployeeId(memberDto.getUserId());
        if(findUser){
            System.out.println("중복된 사용자 입니다.");
            return;
        }

        EmployeeEntity member = new EmployeeEntity();
        member.setEmployeeId(memberDto.getUserId());
        member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        member.setEmployeeName(memberDto.getName());
        member.setHireDate(LocalDate.parse(memberDto.getHireDate()));
        member.setPosition(Position.valueOf(memberDto.getPosition()));
        member.setAccessPermission(Role.ROLE_USER);
        member.setPasswordAnswer(memberDto.getPasswordAnswer());
        employeeRepository.save(member);
    }

//    public MemberDto.Get findByEmployeeId(String employeeId) {
//        MemberEntity findMember = memberRepository.findByEmployeeId(employeeId);
//        MemberDto.Get member = new MemberDto.Get();
//
//
//    }
}
