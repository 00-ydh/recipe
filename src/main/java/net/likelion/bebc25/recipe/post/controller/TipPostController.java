package net.likelion.bebc25.recipe.post.controller;

import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/tip")
public class TipPostController {

    private final PostService postService;

    public TipPostController(PostService postService) {
        this.postService = postService;
    }

    // 요리 꿀팁 리스트 보여주는 컨트롤러
    @GetMapping("/list")
    public String getTipList(Model model) {
        List<PostDto> tips = postService.getTipPosts(2);
        model.addAttribute("posts", tips);
        return "board/tip-list";
    }

    // 요리 꿀팁 작성 화면 보여주는 컨트롤러
    @GetMapping("/write.html")
    public String getTipWrite(@ModelAttribute("postForm") PostDto post) {
        return "board/tip-write";
    }

    // 요리 꿀팁 저장 - 폼 제출 시 DB에 저장 후 목록 페이지로 이동
    // @ModelAttribute: 폼에서 입력한 값을 PostDto에 자동으로 담아줌
    // postType=2 로 꿀팁 게시판임을 명시
    // redirect: 저장 완료 후 목록 URL로 새로 요청 (새로고침 시 중복 저장 방지)
    @PostMapping("/write")
    public String writeTip(@ModelAttribute("postForm") PostDto postDto) {
        postDto.setPostType(2);
        postDto.setMemberId(1); // 임시 (로그인 구현 후 세션에서 가져올 것)
        postService.writePost(postDto);
        return "redirect:/tip/list.html";
    }

    // 요리 꿀팁 상세 보여주는 컨트롤러
    @GetMapping("/detail.html")
    public String getTipDetail(@RequestParam("id") int id, Model model) {
        PostDto tip = postService.getPost(id);
        model.addAttribute("tip", tip);
        return "board/tip-detail";
    }

    // 요리 꿀팁 수정 화면 보여주는 컨트롤러
    @GetMapping("/edit.html")
    public String getTipEdit(@RequestParam("id") int id, Model model) {
        PostDto tip = postService.getPost(id);
        model.addAttribute("postForm", tip);
        return "board/tip-write";
    }

    // 요리 꿀팁 수정 요청 처리하는 컨트롤러
    @PostMapping("/edit")
    public String editTip(@ModelAttribute("postForm") PostDto postDto) {
        postService.editPost(postDto);
        return "redirect:/tip/detail.html?id=" + postDto.getId();
    }

    // 요리 꿀팁 삭제
    @PostMapping("/delete")
    public String deleteTip(@RequestParam int id) {
        postService.removePost(id);
        return "redirect:/tip/list.html";
    }
}
