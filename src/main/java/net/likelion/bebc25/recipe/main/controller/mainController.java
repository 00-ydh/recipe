package net.likelion.bebc25.recipe.main.controller;

import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.main.dto.MainDto;
import net.likelion.bebc25.recipe.main.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class mainController {
    private final MainService mainService;


    public mainController(MainService mainService) {
        this.mainService = mainService;
    }

    //  메인페이지 보여주는 컨트롤러
   // "/" GET 요청 → 메인페이지(index.html) 반환
//    @GetMapping("/")
//    public String mainPage() {
//        return "index";
//    }


    // 메인페이지 보여주는 컨트롤러
    @GetMapping("/")
    public String mainPage(Model model) {
        // 명예의 전당: 기간 x 좋아요 많은 레시피
        List<MainDto> topRecipes = mainService.getTopRecipes();
        model.addAttribute("topRecipes", topRecipes);

        // 이달의 추천: 한 달 이내 좋아요 많은 레시피
        List<MainDto> monthlyRecipes = mainService.getMonthlyTopRecipes();
        model.addAttribute("monthlyRecipes", monthlyRecipes);

        // 최신 요리 꿀팁: 최신순 6개
        List<MainDto> latestTips = mainService.getLatestTipPosts();
        model.addAttribute("latestTips", latestTips);

        return "index";
    }
}

