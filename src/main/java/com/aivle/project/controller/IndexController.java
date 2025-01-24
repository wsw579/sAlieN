package com.aivle.project.controller;

import com.aivle.project.enums.Role;
import com.aivle.project.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        try {
            Role userRole = Role.valueOf(UserContext.getCurrentUserRole());
            if (Role.ROLE_ADMIN.equals(userRole)) {
                return "main/index_admin";
            } else {
                return "main/index_user";
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // 역할이 없는 경우, 또는 잘못된 값일 경우 처리
            return "error/unauthorized"; // 에러페이지
        }
    }


}
