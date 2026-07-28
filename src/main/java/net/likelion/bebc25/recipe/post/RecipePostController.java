package net.likelion.bebc25.recipe.post;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipePost")
public class RecipePostController {


    // recip-list 화면 보여주는 컨트롤러
    @GetMapping("/recipe-list.html")
    public String getRecipeList(){
        return "board/recipe-list";

    }


}
