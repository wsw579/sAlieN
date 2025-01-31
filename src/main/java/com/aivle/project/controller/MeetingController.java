package com.aivle.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeetingController {

    @GetMapping("/meeting_room_1")
    public String meetingRoom1(Model model) {

        String url = "https://zep.us/play/6PBdRZ";

        model.addAttribute("url", url);

        return "meeting/meeting_room_1";

    }


    @GetMapping("/meeting_room_2")
    public String meetingRoom2(Model model) {

        String url = "https://zep.us/play/AO9Gg5";

        model.addAttribute("url", url);

        return "meeting/meeting_room_2";

    }



    @GetMapping("/meeting_room_3")
    public String meetingRoom3(Model model) {

        String url = "https://zep.us/play/Gp9dqz";

        model.addAttribute("url", url);

        return "meeting/meeting_room_3";

    }




}
