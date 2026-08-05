package net.likelion.bebc25.recipe.post.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDto {
    // 게시글 번호
    private int id;

    // 멤버 아이디
    private int memberId;

    // 원래 파일 이름!!!!!
    private String originalFilename;

    // 이미지 파일 타입
    private String contentType;

    // 대표 이미지 DB저장 장소!!!!!!!!
    private String mainImage;

    // 업로드할 때 사용
    private MultipartFile file;

    // 카테고리 ID: 한식 양식 중식 일식
    @Positive(message = "카테고리를 선택해주세요")
    private int categoryId;

    // 제목
    // NotBlank의 메세지는 바뀔 수 있음
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    // 내용
    // NotBlank의 메세지는 바뀔 수 있음
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    // 조회수
    private int viewCount;

    // 작성일자
    private LocalDateTime createdAt;

    // 게시판 타입
    // 1. 레시피 게시판 / 2. 요리꿀팁 게시판
    private int postType;

    // 카테고리 이름
    private String categoryName;

    // 작성자 이름
    private String memberName;

    // 좋아요 수 (LIKE 테이블 COUNT)
    private int likeCount;
}
