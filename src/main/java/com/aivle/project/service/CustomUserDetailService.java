package com.aivle.project.service;

import com.aivle.project.config.CustomUserDetails;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeEntity member = employeeRepository.findByEmployeeId(username);

        if(member != null){
            return new CustomUserDetails(member);
        }
        return null;
    }
}
