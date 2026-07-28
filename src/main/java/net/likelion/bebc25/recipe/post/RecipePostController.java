package net.likelion.bebc25.recipe.post;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipePost")
public class RecipePostController {


    // recipe-list 화면 보여주는 컨트롤러
    @GetMapping("/recipe-list.html")
    public String getRecipeList(){
        return "board/recipe-list";
    }

    // recipe-details 화면 보여주는 컨트롤러
    @GetMapping("/recipe-details.html")
    public String getRecipeDetails(){
        return "redirect:/board/recipe-details.html";
    }

    // recipe-write 화면 보여주는 컨트롤러
    @GetMapping("/recipe-write.html")
    public String getRecipeWriteList(){
        return "board/recipe-write";

    }

    // todayMenu 화면 보여주는 컨트롤러
    @GetMapping("todayMenu.html")
    public String getTodayMenu(){
        return "board/todayMenu";
    }

    // 요리 꿀팁 리스트 보여주는 컨트롤러
    @GetMapping("tip-list.html")
    public String getTipList(){
        return "board/tip-list";
    }

    // 요리 꿀팁 작성하는 화면을 보여주는 컨트롤러
    @GetMapping("tip-write.html")
    public String getTipWrite(){
        return "board/tip-write";
    }

}
