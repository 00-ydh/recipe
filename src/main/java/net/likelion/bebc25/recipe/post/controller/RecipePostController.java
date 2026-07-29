package net.likelion.bebc25.recipe.post.controller;

import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/recipe")
public class RecipePostController {
    private final PostService postService;

    public RecipePostController(PostService postService) {
        this.postService = postService;
    }

    // recipe-list 화면 보여주는 컨트롤러
    @GetMapping("/list.html")
    public String getRecipeList(Model model) {

        // 게시글 목록 조회(데이터)
        List<PostDto> recipePosts = postService.getRecipePosts();
        model.addAttribute("recipePosts", recipePosts);


        return "board/recipe-list";
    }

    // recipe-details 화면 보여주는 컨트롤러
    @GetMapping("/detail.html")
    public String getRecipeDetails(){
        return "board/recipe-detail";
    }

    // recipe-write 화면 보여주는 컨트롤러
    @GetMapping("/write.html")
    public String getRecipeWriteList(){
        return "board/recipe-write";

    }
}
