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

    @PostMapping
    public String toggleFollow(@RequestParam("targetMemberId") int targetMemberId,
                               HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        int followingId = loginMember.getId();

        if (followService.findFollowById(followingId, targetMemberId) != null) {
            followService.deleteFollowMember(followingId, targetMemberId);
        } else {
            followService.saveFollowMember(followingId, targetMemberId);
        }

        MemberDto targetMember = memberService.getMemberById(targetMemberId);

        String encodedNickname = URLEncoder.encode(targetMember.getName(), StandardCharsets.UTF_8);
        return "redirect:/member/profile/" + encodedNickname;
    }
}
