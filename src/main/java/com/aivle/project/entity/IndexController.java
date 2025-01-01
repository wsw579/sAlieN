package com.aivle.project.entity;

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
        return "main/index";
    }


}
