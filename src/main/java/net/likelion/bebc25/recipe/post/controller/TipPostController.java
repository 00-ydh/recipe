package net.likelion.bebc25.recipe.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tip")
public class TipPostController {

    // 요리 꿀팁 리스트 보여주는 컨트롤러
    @GetMapping("/list.html")
    public String getTipList(){
        return "board/tip-list";
    }

    // 요리 꿀팁 작성하는 화면을 보여주는 컨트롤러
    @GetMapping("/write.html")
    public String getTipWrite(){
        return "board/tip-write";
    }

}


