package net.likelion.bebc25.recipe.follow.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.follow.service.FollowService;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 회원 간의 팔로우 및 언팔로우 요청을 처리하는 컨트롤러입니다.
 */
@Controller
@Slf4j
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;

    public FollowController(FollowService followService, MemberService memberService) {
        this.followService = followService;
        this.memberService = memberService;
    }

    /**
     * 대상 회원을 팔로우하거나 언팔로우(토글) 합니다.
     * 이미 팔로우 중이라면 언팔로우, 팔로우 상태가 아니라면 팔로우를 진행한 뒤 특정 회원의 프로필로 돌아갑니다.
     *
     * @param targetMemberName 팔로우/언팔로우 대상 회원 이름
     * @param session 현재 로그인된 사용자 세션
     * @return 대상 회원의 프로필 리다이렉트 경로
     */
    @PostMapping
    public String toggleFollow(@RequestParam("targetMemberName") String targetMemberName,
                               HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        MemberDto targetMember = memberService.getMemberByName(targetMemberName);
        int targetMemberId = targetMember.getId();
        int followingId = loginMember.getId();

        if (followService.findFollowById(followingId, targetMemberId) != null) {
            followService.deleteFollowMember(followingId, targetMemberId);
        } else {
            followService.saveFollowMember(followingId, targetMemberId);
        }

        String encodedNickname = URLEncoder.encode(targetMember.getName(), StandardCharsets.UTF_8);
        return "redirect:/member/profile/" + encodedNickname;
    }
}
