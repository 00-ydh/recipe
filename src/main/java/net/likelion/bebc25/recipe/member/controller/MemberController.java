package net.likelion.bebc25.recipe.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.exception.DuplicateEmailException;
import net.likelion.bebc25.recipe.exception.DuplicateNameException;
import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.follow.service.FollowService;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.service.MemberService;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PostService postService;
    private final FollowService followService;

    public MemberController(MemberService memberService, PostService postService, FollowService followService) {
        this.memberService = memberService;
        this.postService = postService;
        this.followService = followService;
    }

    /**
     * (삭제 예정)
     * 비밀번호 찾기 양식 화면을 반환합니다.
     * @param memberDto 폼과 바인딩할 빈 회원 객체
     * @return 비밀번호 찾기 페이지 뷰 경로
     */
    @GetMapping("/find-account")
    public String getFindAccountForm(@ModelAttribute("member") MemberDto memberDto) {
        return "member/find-account";
    }

    /**
     * (삭제 예정)
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
            return "redirect:/";
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
        return "member/register";
    }

    /**
     * 회원가입을 처리합니다. 중복된 계정이 있을 경우 가입을 허용하지 않습니다.
     * 입력값 검증 실패 시 가입 폼으로 돌아갑니다.
     * @param memberDto 회원 가입 할 회원 정보 DTO
     * @param bindingResult 검증 오류 결과 객체
     * @return 검증 실패/중복 시 가입 폼 경로, 가입 성공 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("member") MemberDto memberDto,
                           @RequestParam(value = "passwordConfirm", required = false) String passwordConfirm,
                           BindingResult bindingResult) {
        if (memberDto.getPassword() != null && !memberDto.getPassword().equals(passwordConfirm)) {
            bindingResult.rejectValue("password", "mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if(bindingResult.hasErrors()){
            return "member/register";
        }

        try {
            memberService.register(memberDto);
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "member/register";
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "member/register";
        }

        return "redirect:/member/login";
    }

    /**
     * 마이페이지 화면을 반환합니다.
     * @param session 로그인한 사용자 세션
     * @param model 로그인한 사용자, 사용자가 쓴 레시피, 팁을 전달하는 Model 객체
     * @return 로그인 한 사용자는 마이페이지 뷰 경로, 로그인 하지 않은 사용자는 로그인 페이지 뷰 경로
     */
    @GetMapping("/mypage")
    public String getMyPageForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/member/login";
        }

        List<PostDto> myFollowingRecipes = followService.myFollowingMembersRecipes(loginMember.getId());
        List<PostDto> myRecipes = postService.getRecipePost(loginMember.getId());
        List<PostDto> myTips = postService.getTipPost(loginMember.getId());
        List<MemberDto> myFollowing = followService.myFollowingMembers(loginMember.getId());

        model.addAttribute("member", loginMember);
        model.addAttribute("myRecipes", myRecipes);
        model.addAttribute("myTips", myTips);
        model.addAttribute("myFollowing", myFollowing);
        model.addAttribute("myFollowingRecipes", myFollowingRecipes);
        model.addAttribute("followerCount", followService.getFollowerCount(loginMember.getId()));
        model.addAttribute("recipeCount", myRecipes.size());

        return "member/mypage";
    }

    /**
     * 다른 회원의 프로필 화면을 반환합니다.
     * @param model 다른 회원의 레시피와 팁을 전달하는 Model 객체
     * @return 프로필 페이지 뷰 경로
     */
    @GetMapping("/profile/{name}")
    public String getProfileForm(@PathVariable("name") String name, Model model, HttpSession session) { // 임시
        MemberDto memberDto = memberService.getMemberByName(name);
        // 존재하지 않는 회원 조회 시 메인페이지로 이동
        if(memberDto == null) {
            return "redirect:/";
        }

        // 로그인 한 사용자 불러오기
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        boolean isFollowing = false;
        // 로그인 한 사용자일 경우에 체크할 로직
        if (loginMember != null) {
            if (memberDto.getId() == loginMember.getId()) {
                return "redirect:/member/mypage";
            }
            isFollowing = followService.findFollowById(loginMember.getId(), memberDto.getId()) != null;
        }


        List<PostDto> myRecipes = postService.getRecipePost(memberDto.getId());

        model.addAttribute("member", memberDto);
        model.addAttribute("myRecipes", myRecipes);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("recipeCount", myRecipes.size());
        model.addAttribute("followerCount", followService.getFollowerCount(memberDto.getId()));

        return "member/profile";
    }

    /**
     * 내 정보 수정 화면을 반환합니다.
     * @param session 로그인된 회원 세션
     * @param model 로그인된 회원 정보를 전달하는 Model 객체
     * @return 내 정보 수정 페이지 뷰 경로
     */
    @GetMapping("/edit")
    public String getUserEditForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        model.addAttribute("member", loginMember);

        return "member/user-edit";
    }


    /**
     * 로그인 한 회원의 별명을 수정합니다.
     * @param memberDto 화면에서 전달받은 수정할 회원 정보
     * @param bindingResult 별명 입력값 유효성 검증 결과
     * @param session 현재 로그인한 회원 세션
     * @return 검증 실패 시 회원정보 수정 뷰 경로, 성공 시 마이페이지 리다이렉트
     */
    @PostMapping("/edit/name")
    public String editName(@Valid @ModelAttribute("member") MemberDto memberDto,
                           BindingResult bindingResult,
                           HttpSession session) {

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (bindingResult.hasFieldErrors("name")) {
            return "member/user-edit";
        }

        try {
            loginMember.setName(memberDto.getName());
            memberService.editName(loginMember.getId(), memberDto.getName());
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "member/user-edit";
        }

        loginMember.setName(memberDto.getName());
        session.setAttribute("loginMember", loginMember);

        return "redirect:/member/mypage";
    }

    /**
     * 로그인 한 회원의 비밀번호를 수정합니다.
     * @param memberDto 화면에서 전달받은 수정할 회원 정보
     * @param bindingResult 비밀번호 입력값 유효성 검증 결과
     * @param session 현재 로그인한 회원 세션
     * @return 검증 실패 시 회원정보 수정 뷰 경로, 성공 시 마이페이지 리다이렉트
     */
    @PostMapping("/edit/password")
    public String editPassword(@Valid @ModelAttribute("member") MemberDto memberDto,
                               BindingResult bindingResult,
                               HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (bindingResult.hasFieldErrors("password")) {
            return "member/user-edit";
        }

        memberService.editPassword(loginMember.getId(), memberDto.getPassword());

        loginMember.setPassword(memberDto.getPassword());
        session.setAttribute("loginMember", loginMember);

        return "redirect:/member/mypage";
    }

    /**
     * 회원 로그아웃을 합니다.
     * @param session 로그인 한 회원 세션
     * @return 메인 페이지 뷰 경로
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        session.invalidate();
        memberService.deleteMemberById(loginMember.getId());

        return  "redirect:/";
    }
}
