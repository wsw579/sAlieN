package com.aivle.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class ErrorController {
    @GetMapping("/errorPage")
    public String showErrorPage(Model model, @ModelAttribute("errorMessage") String errorMessage) {
        model.addAttribute("errorMessage", errorMessage != null ? errorMessage : "알 수 없는 오류가 발생했습니다.");
        return "error/errorPage"; // errorPage.mustache 렌더링
    }
}
