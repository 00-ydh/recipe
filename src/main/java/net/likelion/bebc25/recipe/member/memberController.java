package net.likelion.bebc25.recipe.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class memberController {

    @GetMapping("/find-account")
    public String findAccount() {
        return "member/find-account.html";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "member/register.html";
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "member/mypage.html";
    }
}
