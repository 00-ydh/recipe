package net.likelion.bebc25.recipe.reply.controller;

import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
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
                             @ModelAttribute RequestDTO requestDTO) {

        requestDTO.setMemberId(1L);
        // 댓글 저장
        replyService.writeReply(requestDTO);

        //어느 게시판이냐에 따라 리다이렉트 경로를 다르게 설정!
        if ("tip".equals(boardType)) {
            // 팁 게시판 상세로 이동
            return "redirect:/tip/detail?id=" + requestDTO.getPostId();
        } else {
            // 레시피 게시판 상세로 이동
            return "redirect:/recipe/detail?id=" + requestDTO.getPostId();
        }
    }


    @PostMapping("/delete")
    public String deleteReply(@RequestParam("id") Long id,
                              @RequestParam("postId") Long postId,
                              @RequestParam("boardType") String boardType) {

        // 1. 댓글 삭제
        replyService.deleteReply(id);

        // 2. 어느 게시판이었느냐에 따라 상세 페이지로 리다이렉트
        if ("tip".equals(boardType)) {
            return "redirect:/tip/detail?id=" + postId;
        } else {
            return "redirect:/recipe/detail?id=" + postId;
        }
    }

}