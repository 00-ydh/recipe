package net.likelion.bebc25.recipe.post.controller;

import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/today")
public class TodayMenuPostController {
    private final PostService postService;

    public TodayMenuPostController(PostService postService) {
        this.postService = postService;
    }

    // todayMenu 화면 보여주는 컨트롤러
    @GetMapping("/list")
    public String getTodayMenu(){
        return "board/today-menu";
    }

    @GetMapping("/recommend")
    public String recommend(@RequestParam int categoryId, Model model){
        PostDto postDto = postService.getRandomRecipePost(categoryId);

        if(postDto == null){
            model.addAttribute("message", "해당 카테고리의 게시글이 없습니다.");
        }
        model.addAttribute("post", postDto);
        return "board/today-menu";

    }
}
