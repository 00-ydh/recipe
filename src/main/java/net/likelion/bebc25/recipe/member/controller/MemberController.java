package net.likelion.bebc25.recipe.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.exception.DuplicateEmailException;
import net.likelion.bebc25.recipe.exception.DuplicateNameException;
import net.likelion.bebc25.recipe.follow.dto.FollowDto;
import net.likelion.bebc25.recipe.follow.service.FollowService;
import net.likelion.bebc25.recipe.good.service.GoodService;
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

/**
 * 회원 관련 요청(회원 가입, 로그인, 정보 수정, 탈퇴, 프로필, 마이페이지 등)을 처리하는 컨트롤러입니다.
 */
@Controller
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PostService postService;
    private final FollowService followService;
    private final GoodService goodService;

    public MemberController(MemberService memberService, PostService postService, FollowService followService, GoodService goodService) {
        this.memberService = memberService;
        this.postService = postService;
        this.followService = followService;
        this.goodService = goodService;
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
     * 이미 로그인 한 사용자가 접근할 경우 메인 화면으로 이동합니다.
     *
     * @param session 현재 로그인 상태를 확인하기 위한 세션
     * @return 로그인 페이지 뷰 경로 또는 메인 화면 리다이렉트
     */
    @GetMapping("/login")
    public String getLoginForm(HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember != null) {
            return "redirect:/";
        }

        return "member/login";
    }

    /**
     * 사용자 로그인 요청을 처리합니다.
     * 입력받은 이메일과 비밀번호를 검증하여 성공 시 메인 화면으로 이동하고 세션에 회원 정보를 저장합니다.
     *
     * @param email 입력받은 이메일
     * @param password 입력받은 비밀번호
     * @param session 로그인 성공 시 회원 정보를 저장할 세션 객체
     * @param model 로그인 실패 시 에러 메시지를 전달할 객체
     * @return 성공 시 메인 화면, 실패 시 로그인 페이지 뷰 경로
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
     *
     * @param memberDto 폼과 바인딩할 빈 회원 정보 객체
     * @return 회원가입 페이지 뷰 경로
     */
    @GetMapping("/register")
    public String getRegisterForm(@ModelAttribute("member") MemberDto memberDto) {
        return "member/register";
    }

    /**
     * 회원가입을 처리합니다.
     * 중복된 계정 또는 이름이 있을 경우 가입을 허용하지 않습니다.
     * 입력값 검증 실패 시 가입 폼으로 돌아갑니다.
     *
     * @param memberDto 회원가입 할 회원 정보 DTO
     * @param passwordConfirm 비밀번호 확인을 위한 입력값
     * @param bindingResult 검증 오류 결과를 담는 객체
     * @return 검증 실패/중복 시 회원가입 폼 경로, 가입 성공 시 로그인 페이지로 리다이렉트
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
     * 로그인한 사용자의 마이페이지 화면을 반환합니다.
     * (LoginCheckInterceptor 통과 후 접근 가능)
     *
     * @param session 현재 로그인한 사용자 세션
     * @param model 마이페이지에 출력할 데이터(레시피, 팁, 팔로잉 등)을 담는 객체
     * @return 마이페이지 뷰 경로
     */
    @GetMapping("/mypage")
    public String getMyPageForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        List<PostDto> myFollowingRecipes = followService.myFollowingMembersRecipes(loginMember.getId());
        List<PostDto> myRecipes = postService.getRecipePost(loginMember.getId());
        List<PostDto> myTips = postService.getTipPost(loginMember.getId());
        List<MemberDto> myFollowing = followService.myFollowingMembers(loginMember.getId());
        List<PostDto> myScarpRecipes = goodService.getRecipes(loginMember.getId());
        List<PostDto> myScarpTips = goodService.getTips(loginMember.getId());

        model.addAttribute("member", loginMember);
        model.addAttribute("myRecipes", myRecipes);
        model.addAttribute("myTips", myTips);
        model.addAttribute("myFollowing", myFollowing);
        model.addAttribute("myFollowingRecipes", myFollowingRecipes);
        model.addAttribute("followerCount", followService.getFollowerCount(loginMember.getId()));
        model.addAttribute("recipeCount", myRecipes.size());
        model.addAttribute("myScarpRecipes", myScarpRecipes);
        model.addAttribute("myScarpTips", myScarpTips);

        return "member/mypage";
    }

    /**
     * 특정 회원의 프로필 화면을 반환합니다.
     * @param name 조회할 대상 회원의 이름
     * @param model 대상 회원의 레시피 및 팔로워 정보를 담는 객체
     * @param session 현재 로그인한 사용자 세션 (팔로우 버튼 상태 확인)
     * @return 프로필 뷰 경로, 존재하지 않으면 메인 화면으로 리다이렉트
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
     * (LoginCheckInterceptor 통과 후 접근 가능)
     *
     * @param session 현재 로그인된 회원 세션
     * @param model 현재 로그인된 회원 정보를 전달하는 Model 객체
     * @return 내 정보 수정 페이지 뷰 경로
     */
    @GetMapping("/edit")
    public String getUserEditForm(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        model.addAttribute("member", loginMember);

        return "member/user-edit";
    }


    /**
     * 로그인한 회원의 이름을 수정합니다.
     * 입력값의 유효성 및 이름 중복 여부를 검증하여 성공 시 DB와 현재 세션의 정보를 갱신합니다.
     * (LoginCheckInterceptor 통과 후 접근 가능)
     *
     * @param memberDto 화면에서 전달받은 수정할 회원 정보
     * @param bindingResult 별명 입력값 유효성 검증 결과를 담는 객체
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
     * 유효성 검증 성공 시 DB와 현재 세션 정보를 갱신합니다.
     *
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
     * 회원 로그아웃 처리를 합니다.
     * 현재 사용자 세션을 무효화(삭제)합니다
     *
     * @param session 현재 로그인된 회원 세션
     * @return 메인 페이지 뷰 경로
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /**
     * 회원 탈퇴를 처리합니다.
     * DB에서 해당 회원의 데이터를 삭제한 후, 현재 로그인된 세션도 무효화합니다.
     *
     * @param session 현재 로그인 된 회원 세션
     * @return 메인 페이지 뷰 경로
     */
    @PostMapping("/delete")
    public String delete(HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        session.invalidate();
        memberService.deleteMemberById(loginMember.getId());

        return  "redirect:/";
    }
}

