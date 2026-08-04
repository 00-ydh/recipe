package net.likelion.bebc25.recipe.post.controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.good.service.GoodService;
import net.likelion.bebc25.recipe.member.dto.MemberDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.service.PostService;
import org.springframework.http.MediaType;
import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.service.ReplyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/tip")
public class TipPostController {

    private final PostService postService;
    private final String uploadDir = Paths.get("C:","Programming","TUI_Editor","upload").toString();
    private final ReplyService replyService;
    private final GoodService goodService;

    public TipPostController(PostService postService, ReplyService replyService, GoodService goodService) {
        this.postService = postService;
        this.replyService = replyService;
        this.goodService = goodService;


    }

    // 요리 꿀팁 리스트 보여주는 컨트롤러 (페이징 하는거 추가 완료)
    @GetMapping("/list")
    public String getTipList(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = "8") int size,
                             Model model) {

        // 게시글 목록 조회(데이터)
        // size = 페이지당 보여주고 싶은 게시글 수
        List<PostDto> tips = postService.getTipPosts(page, size);

        // 여기서 페이지 계산
        int totalCount = postService.tipPostCount();
        int totalPage = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("posts", tips);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("postCount", totalCount);  // 총 꿀팁 게시글 수
        return "board/tip-list";
    }

    // 요리 꿀팁 작성 화면 보여주는 컨트롤러
    @GetMapping("/write")
    public String getTipWrite(@ModelAttribute("postForm") PostDto post) {
        return "board/tip-write";
    }



    // 요리 꿀팁 저장 - 폼 제출 시 DB에 저장 후 목록 페이지로 이동
    // @ModelAttribute: 폼에서 입력한 값을 PostDto에 자동으로 담아줌
    // postType=2 로 꿀팁 게시판임을 명시
    // redirect: 저장 완료 후 목록 URL로 새로 요청 (새로고침 시 중복 저장 방지)
    @PostMapping("/write")
    public String writeTip(@Valid @ModelAttribute("postForm") PostDto postDto,
                           BindingResult bindingResult,
                           HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if (bindingResult.hasErrors()) {
            return "board/tip-write";
        }

        postDto.setPostType(2);
        postDto.setMemberId(loginMember.getId());
        postService.writePost(postDto);
        return "redirect:/tip/list";
    }


    @GetMapping("/detail")
    public String getTipDetail(@RequestParam("id") int id, HttpSession session, Model model) {
        // 조회수 증가
        postService.viewCount(id);
        PostDto tip = postService.getPost(id);
        model.addAttribute("tip", tip);

        List<ResponseDTO> replies = replyService.getRepliesByPostId(id);
        model.addAttribute("replies", replies);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        boolean isLiked = false;
        boolean isScrapped = false;

        if (loginMember != null) {
            int memberId = loginMember.getId();

            //  팁 게시글 좋아요 상태 확인
            GoodDto likeDto = new GoodDto();
            likeDto.setPostId(id);
            likeDto.setMemberId(memberId);
            likeDto.setLikeType(2); // 좋아요 likeType
            isLiked = goodService.isLiked(likeDto);

            // 팁 게시글 스크랩 상태 확인
            GoodDto scrapDto = new GoodDto();
            scrapDto.setPostId(id);
            scrapDto.setMemberId(memberId);
            scrapDto.setLikeType(3); // 스크랩 likeType
            isScrapped = goodService.isLiked(scrapDto);
        }

        model.addAttribute("isLiked", isLiked);
        model.addAttribute("isScrapped", isScrapped);

        return "board/tip-detail";
    }

    // 요리 꿀팁 수정 화면 보여주는 컨트롤러
    @GetMapping("/edit")
    public String getTipEdit(@RequestParam("id") int id, Model model, HttpSession session) {
        PostDto tip = postService.getPost(id);
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        // 현재 로그인한 회원이 게시글 작성자가 아니면 수정 페이지에 들어갈 수 없다.
        if (loginMember == null || loginMember.getId() != tip.getMemberId()) {
            return "redirect:/tip/detail?id=" + id;
        }

        model.addAttribute("postForm", tip);
        return "board/tip-write";
    }

    // 요리 꿀팁 수정 요청 처리하는 컨트롤러
    @PostMapping("/edit")
    public String editTip(@ModelAttribute("postForm") PostDto postDto, HttpSession session) {
        PostDto savedTip = postService.getPost(postDto.getId());
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        // 현재 로그인한 회원이 게시글 작성자인 경우에만 수정한다.
        if (loginMember != null && loginMember.getId() == savedTip.getMemberId()) {
            postService.editPost(postDto);
        }

        return "redirect:/tip/detail?id=" + postDto.getId();
    }

    // 요리 꿀팁 삭제
    @PostMapping("/delete")
    public String deleteTip(@RequestParam int id, HttpSession session) {
        PostDto tip = postService.getPost(id);
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        // 현재 로그인한 회원이 게시글 작성자인 경우에만 삭제한다.
        if (loginMember.getId() == tip.getMemberId()) {
            postService.removePost(id);
        }

        return "redirect:/tip/list";
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
