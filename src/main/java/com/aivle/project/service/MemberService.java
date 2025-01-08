package com.aivle.project.service;

import com.aivle.project.dto.MemberDto;
import com.aivle.project.entity.MemberEntity;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(MemberDto.Post memberDto){

        // 동일한 id를 가진 user가 존재하는지 검증
        boolean findUser = memberRepository.existsByEmployeeId(memberDto.getUserId());
        if(findUser){
            System.out.println("중복된 사용자 입니다.");
            return;
        }

        MemberEntity member = new MemberEntity();
        member.setEmployeeId(memberDto.getUserId());
        member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        member.setEmployeeName(memberDto.getName());
        member.setHireDate(LocalDate.parse(memberDto.getHireDate()));
        member.setPosition(Position.valueOf(memberDto.getPosition()));
        member.setAccessPermission(Role.ROLE_USER);
        member.setPasswordAnswer(memberDto.getPasswordAnswer());
        memberRepository.save(member);
    }
}
