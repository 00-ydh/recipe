package net.likelion.bebc25.recipe.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.service.MemberService;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PostService postService;

    public MemberController(MemberService memberService, PostService postService) {
        this.memberService = memberService;
        this.postService = postService;
    }

    /**
     * 비밀번호 찾기 양식 화면을 반환합니다.
     * @param memberDto 폼과 바인딩할 빈 회원 객체
     * @return 비밀번호 찾기 페이지 뷰 경로
     */
    @GetMapping("/find-account")
    public String getFindAccountForm(@ModelAttribute("member") MemberDto memberDto) {
        return "member/find-account";
    }

    /**
     * 회원의 이메일, 별명이 일치하면 비밀번호를 보여줍니다.
     * @param memberDto 비밀번호를 찾을 회원 정보 객체
     * @param model 결과 메시지를 전달하기 위한 model 객체
     * @return 비밀번호 찾기 결과가 포함된 뷰 경로
     */
    @PostMapping("/find-account")
    public String findAccount(@ModelAttribute("member") MemberDto memberDto, Model model) {
        String resultPassword = memberService.findPassword(memberDto.getEmail(), memberDto.getName());

        model.addAttribute("resultMessage", resultPassword);
        return "member/find-account";
    }

    /**
     * 로그인 양식 화면을 반환합니다.
     * @return 로그인 페이지 뷰 경로
     */
    @GetMapping("/login")
    public String getLoginForm(HttpSession session) {
        // 세션에 로그인 된 회원 정보가 있는 지 확인
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        // 로그인 된 상태이면 메인화면으로 리다이렉트
        if (loginMember != null) {
            return "redirect:/";
        }

        // 비로그인 상태이면 로그인 페이지 반환
        return "member/login";
    }

    /**
     * 이메일과 비밀번호를 입력받아 검증 성공 시 메인 화면으로 이동합니다.
     * @param email 입력 받은 이메일
     * @param password 입력 받은 비밀번호
     * @return 성공 시 메인 화면, 실패 시 로그인 페이지
     */
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        MemberDto loginMember =  memberService.login(email, password);

        if (loginMember != null) {
            session.setAttribute("loginMember", loginMember);
            return "redirect:/member/mypage";
        } else {
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 맞지 않습니다.");
            return "member/login";
        }
    }

    /**
     * 회원 가입 양식 화면을 반환합니다.
     * @param memberDto 폼과 바인딩할 빈 회원 정보 객체
     * @return 회원가입 페이지 뷰 경로
     */
    @GetMapping("/register")
    public String getRegisterForm(@ModelAttribute("member") MemberDto memberDto) {
        return "member/register.html";
    }

    /**
     * 회원가입을 처리합니다. 중복된 계정이 있을 경우 가입을 허용하지 않습니다.
     * 입력값 검증 실패 시 가입 폼으로 돌아갑니다.
     * @param memberDto 회원 가입 할 회원 정보 DTO
     * @param bindingResult 검증 오류 결과 객체
     * @param model 화면에 에러 메시지를 전달하기 위한 Model 객체
     * @return 검증 실패/중복 시 가입 폼 경로, 가입 성공 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("member") MemberDto memberDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            return "member/register";
        }

        if(!(memberService.register(memberDto))) {
            model.addAttribute("errorMessage", "중복된 이메일입니다.");
            return "member/register";
        }

        return "redirect:/member/login";
    }

    /**
     * 마이페이지 화면을 반환합니다.
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/mypage")
    public String getMyPageForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("member", loginMember);
//        List<PostDto> myRecipes = postService.getRecipesByMemberId(loginMember.getId());
//        model.addAttribute("myRecipes", myRecipes);

        return "member/mypage";
    }

    @GetMapping("/profile")
    public String profile() {
        return "member/profile";
    }

    /**
     * 내 정보 수정 화면을 반환합니다.
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String getUserEditForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        model.addAttribute("member", loginMember);

        return "member/user-edit";
    }

    /**
     * 회원 별명을 수정합니다.
     * @param name
     * @param session
     * @return
     */
    @PostMapping("/edit/name")
    public String editName(@RequestParam String name, HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        loginMember.setName(name);

        memberService.editMember(loginMember);

        session.setAttribute("loginMember", loginMember);

        return "redirect:/member/mypage";
    }

    /**
     * 회원 비밀번호를 수정합니다.
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/edit/password")
    public String editPassword(@RequestParam String password, HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        loginMember.setPassword(password);

        memberService.editMember(loginMember);

        session.setAttribute("loginMember", loginMember);

        return "redirect:/member/mypage";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "";
    }
}
