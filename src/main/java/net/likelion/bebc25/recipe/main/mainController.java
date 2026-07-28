package net.likelion.bebc25.recipe.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {

    @GetMapping("/")
        public String mainPage(){
            return "main";
        }
    }

