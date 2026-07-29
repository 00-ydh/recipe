package net.likelion.bebc25.recipe.post.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/recipe")
public class RecipePostController {

    // recipe-list 화면 보여주는 컨트롤러
    @GetMapping("/list.html")
    public String getRecipeList(){
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
