package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @GetMapping("/signup")
    public String signup() {
        return "user/signup";  // signup.mustache 렌더링
    }

    @GetMapping("/api/generateEmployeeId")
    @ResponseBody // 반환값을 JSON으로 처리
    public ResponseEntity<Map<String, String>> generateUserId(@RequestParam("year") int year) {
        Map<String, String> response = new HashMap<>();
        String employeeId = signupService.makeNewEmployeeId(year+"");
        response.put("employeeId", employeeId); // 예시 응답
        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/signup/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody EmployeeDto.Post employeeDto) {
        try {
            signupService.registerUser(employeeDto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
