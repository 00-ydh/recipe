package net.likelion.bebc25.recipe.reply.controller;

import jakarta.servlet.http.HttpSession;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.service.ReplyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/write")
    public String writeReply(@RequestParam("boardType") String boardType,
                             @ModelAttribute RequestDTO requestDTO,
                             HttpSession session) {

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        requestDTO.setMemberId(loginMember.getId());

        replyService.writeReply(requestDTO);

        if ("tip".equals(boardType)) {
            return "redirect:/tip/detail?id=" + requestDTO.getPostId();
        } else {
            return "redirect:/recipe/detail?id=" + requestDTO.getPostId();
        }
    }

    @PostMapping("/delete")
    public String deleteReply(@RequestParam("id") int id,
                              @RequestParam("postId") int postId,
                              @RequestParam("boardType") String boardType,
                              HttpSession session) { //HttpSession 파라미터 추가

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        ResponseDTO reply = replyService.findById(id);

        if (reply != null && reply.getMemberId() == loginMember.getId()) {
            replyService.deleteReply(id);
        } else {
            System.out.println(">>> [삭제 실패] 본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        if ("tip".equals(boardType)) {
            return "redirect:/tip/detail?id=" + postId;
        } else {
            return "redirect:/recipe/detail?id=" + postId;
        }
    }

}