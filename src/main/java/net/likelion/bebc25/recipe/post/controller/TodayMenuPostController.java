package net.likelion.bebc25.recipe.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/today")
public class TodayMenuPostController {
    // todayMenu 화면 보여주는 컨트롤러
    @GetMapping("/list")
    public String getTodayMenu(){
        return "board/today-menu";
    }
}
