package net.likelion.bebc25.recipe.post.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.service.ReplyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/recipe")
public class RecipePostController {
    private final PostService postService;
    private final ReplyService replyService;
    // 다들 각자 설정
    //@Value("${file.upload-dir}")
    private final String uploadDir = Paths.get("D:","Programming","TUI_Editor","upload").toString();

    public RecipePostController(PostService postService, ReplyService replyService) {
        this.postService = postService;
        this.replyService = replyService;

    }

    // recipe-list 화면 보여주는 컨트롤러
    @GetMapping("/list")
    public String getRecipeList(Model model) {

        // 게시글 목록 조회(데이터)
        List<PostDto> recipePosts = postService.getRecipePosts();
        model.addAttribute("recipePosts", recipePosts);
        log.info("recipePosts.size() = {}", recipePosts.size());

        return "board/recipe-list";
    }

    // recipe-details 화면 보여주는 컨트롤러
    @GetMapping("/detail")
    public String getRecipeDetails(@RequestParam("id") int id, Model model) {
        PostDto postDto = postService.getRecipe(id);
        model.addAttribute("post", postDto);
        List<ResponseDTO> replies = replyService.getRepliesByPostId( id);
        model.addAttribute("replies", replies);
        return "board/recipe-detail";
    }

    // recipe 등록 화면을 요청하는 컨트롤러
    @GetMapping("/write")
    public String getRecipeWriteForm(@ModelAttribute("recipePostForm") PostDto post){
        return "board/recipe-write";
    }
    // recipe 게시글 등록 요청을 처리하는 컨트롤러
    @PostMapping("/write")
    public String writeRecipePost(@Valid @ModelAttribute("recipePostForm") PostDto post, BindingResult bindingResult){
        post.setMemberId(1);
        // 레시피 post_type
        post.setPostType(1);
        // memberid 확인용
//        log.info("member = {} ", post.getMemberId());

        // 검증에 실패했을 경우
        if(bindingResult.hasErrors()){
            return "board/recipe-write";
        }

        postService.writePost(post);
        return "redirect:/recipe/list";
    }

    // 레시피 게시글을 수정하는 화면으로 가는 컨트롤러
    @GetMapping("/edit")
    public String getRecipeEditForm(@RequestParam("id") int id, Model model){
        PostDto post = postService.getRecipe(id);
        model.addAttribute("recipePostForm", post);
        return "board/recipe-write";
    }

    // 레시피 게시글을 수정 요청을 처리하는 컨트롤러
    @PostMapping("/edit")
    public String editRecipePost(@Valid @ModelAttribute("recipePostForm") PostDto post, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "board/recipe-write";
        }
        postService.editPost(post);
        return "redirect:/member/mypage";
    }

    // 레시피 게시글을 삭제 요청을 처리하는 컨트롤러
    @PostMapping("/delete")
    public String deleteRecipePost(@RequestParam int id){
        postService.removePost(id);
        return "redirect:/member/mypage";
    }

    @PostMapping("/image-upload")
    @ResponseBody
    public String uploadEditorImage(@RequestParam("image") MultipartFile image){
        log.info("이미지 업로드 요청");
        log.info("파일명 = {}", image.getOriginalFilename());
        if(image.isEmpty()){
            return "";
        }
        String orgFilename = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);
        String saveFilename = uuid + "." + extension;
        String fileFullPath = Paths.get(uploadDir, saveFilename).toString();

        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            image.transferTo(new File(fileFullPath));

            return saveFilename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(value = "/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    @ResponseBody
    public byte[] printEditorImage(@RequestParam final String filename) {
        // 업로드된 파일의 전체 경로
        String fileFullPath = Paths.get(uploadDir, filename).toString();

        // 파일이 없는 경우 예외 throw
        File uploadedFile = new File(fileFullPath);
        if (uploadedFile.exists() == false) {
            throw new RuntimeException();
        }

        try {
            // 이미지 파일을 byte[]로 변환 후 반환
            byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
            return imageBytes;

        } catch (IOException e) {
            // 예외 처리는 따로 해주는 게 좋습니다.
            throw new RuntimeException(e);
        }
    }
}
