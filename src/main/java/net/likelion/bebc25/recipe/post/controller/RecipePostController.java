package net.likelion.bebc25.recipe.post.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.good.service.GoodService;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.service.ReplyService;
import net.likelion.bebc25.recipe.file.FileStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Member;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/recipe")
public class RecipePostController {
    private final PostService postService;
    private final ReplyService replyService;
    private final GoodService goodService;
    private final FileStore fileStore;

    // 다들 각자 설정
    // ToastUI에디터 이미지 저장 경로 -> 이미지 하나 업로드랑 같이 가능
    // application.properties에 따로 설정
    @Value("${file.uploadDir}")
    private String uploadDir;

    public RecipePostController(PostService postService,
                                ReplyService replyService,
                                GoodService goodService,
                                @Qualifier("localFileStore") FileStore fileStore) {

        this.postService = postService;
        this.replyService = replyService;
        this.goodService = goodService;
        this.fileStore = fileStore;
    }

    // recipe-list 화면 보여주는 컨트롤러
    @GetMapping("/list")
    public String getRecipeList(@RequestParam(value = "page", defaultValue = "1")int page,
                                @RequestParam(value = "pageSize", defaultValue = "8") int size,
                                //   처음 열었을때 최신순 적용
                                @RequestParam(value = "type", defaultValue = "latest") String type,
                                @RequestParam(defaultValue = "0") int categoryId,
                                Model model) {

        // 게시글 목록 조회(데이터)
        // size = 페이지당 보여주고 싶은 게시글 수
        List<PostDto> recipePosts = postService.getRecipePosts(categoryId, type, page, size);

        // 여기서 페이지 계산
        int totalCount = postService.recipePostCount();
        int totalPage = (int) Math.ceil((double) totalCount / size);

        // 총 레시피 게시글
        int count = postService.recipePostCount();

        model.addAttribute("recipePosts", recipePosts);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("type", type);
        model.addAttribute("postCount", count);
        model.addAttribute("categoryId", categoryId);

        return "board/recipe-list";
    }

    // recipe-details 화면 보여주는 컨트롤러
    @GetMapping("/detail")
    public String getRecipeDetails(@RequestParam("id") int id, Model model, HttpSession session) {
        PostDto postDto = postService.getRecipe(id);
        List<ResponseDTO> replies = replyService.getRepliesByPostId(id);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        // 로그인이 되어있는 상태일 때 좋아요 관련
        if (loginMember != null) {
            GoodDto goodDto = new GoodDto();
            goodDto.setPostId(id);
            goodDto.setMemberId(loginMember.getId());
            goodDto.setLikeType(1);

            boolean isLiked = goodService.isLiked(goodDto);
            model.addAttribute("isLiked", isLiked);
        } else {
            model.addAttribute("isLiked", false);
        }
        model.addAttribute("post", postDto);
        model.addAttribute("replies", replies);

        return "board/recipe-detail";
    }

    // recipe 등록 화면을 요청하는 컨트롤러
    @GetMapping("/write")
    public String getRecipeWriteForm(@ModelAttribute("recipePostForm") PostDto post){
        return "board/recipe-write";
    }

    // recipe 게시글 등록 요청을 처리하는 컨트롤러
    // reuired = false 파라미터 필수 아니게
    @PostMapping("/write")
    public String writeRecipePost(@Valid @ModelAttribute("recipePostForm") PostDto post
                                  , BindingResult bindingResult
                                  , HttpSession session) throws IOException {
        // 게시글 작성시 로그인 세션
        MemberDto loginMember = (MemberDto)session.getAttribute("loginMember");
        if(loginMember == null){
            return "redirect:/member/login";
        }

        // 검증에 실패했을 경우
        if(bindingResult.hasErrors()){
            return "board/recipe-write";
        }
        // 로그인 된 회원의 Id를 게시글의 작성자 ID로 지정
        post.setMemberId(loginMember.getId());
        // postType을 recipe로 지정
        post.setPostType(1);

        // 파일 객체가 있고, 실제 내용도 있다면
        if(post.getFile() != null && !post.getFile().isEmpty()){
            // fileStore메서드 - 업로드된 파일을 검사하여 UUID으로 저장
            // 그리고 그것을 mainImage DB에 저장
            String mainImage = fileStore.storeFile(post.getFile());
            post.setOriginalFilename(post.getFile().getOriginalFilename());
            post.setMainImage(mainImage);
            post.setContentType(post.getFile().getContentType());
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
    public String editRecipePost(@Valid @ModelAttribute("recipePostForm") PostDto post
                                , BindingResult bindingResult
                                , HttpSession session) throws IOException {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if(loginMember == null){
            return "redirect:/member/login";
        }
        if(bindingResult.hasErrors()){
            return "board/recipe-write";
        }
        PostDto postDto = postService.getRecipe(post.getId());

        if(loginMember.getId() != postDto.getMemberId()){
            return "redirect:/recipe/list";
        }

        // 새로운 파일이 들어온 경우 새로 저장, 없으면 기존 첨부파일 정보 유지
        if(post.getFile() != null && !post.getFile().isEmpty()){
            String storeFilename = fileStore.storeFile(post.getFile());
            post.setOriginalFilename(post.getFile().getOriginalFilename());
            post.setMainImage(storeFilename);
            post.setContentType(post.getFile().getContentType());
        } else {
            post.setOriginalFilename(postDto.getOriginalFilename());
            post.setMainImage(postDto.getMainImage());
            post.setContentType(postDto.getContentType());
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


    @GetMapping("/posts/search")
    public String searchPosts(
            @RequestParam(value = "type", required = false, defaultValue = "title") String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        int size = 10;

        List<PostDto> posts = postService.searchPosts(type, keyword, page, size);
        int totalCount = postService.searchPostCount(type, keyword);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("posts", posts);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "search/search";
    }

}
