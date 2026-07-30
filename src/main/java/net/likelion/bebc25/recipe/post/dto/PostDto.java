package net.likelion.bebc25.recipe.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    // 대표 이미지
    private String mainImage;

    // 카테고리 ID: 한식 양식 중식 일식
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
