package net.likelion.bebc25.recipe.good.controller;

import net.likelion.bebc25.recipe.member.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.good.service.GoodService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GoodController {
    private final GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }


    @PostMapping("/good/toggle")
    public String toggleGood(GoodDto goodDto, HttpSession session) {

        //  세션에서 로그인 정보 가져오기
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        //  로그인을 안 했다면 로그인 페이지로 튕겨내기
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        //  로그인한 회원의 번호(id)를 가져와서 DTO에 세팅
        int memberId = loginMember.getId();
        goodDto.setMemberId(memberId);

        //좋아요 토글 서비스 실행
        goodService.toggleLike(goodDto);

        // 원래 있던 상세 페이지로 리다이렉트
        if ("recipe".equals(goodDto.getPostType())) {
            return "redirect:/recipe/detail?id=" + goodDto.getPostId();
        } else {
            return "redirect:/tip/detail?id=" + goodDto.getPostId();
        }
    }

}
