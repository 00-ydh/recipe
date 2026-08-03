package net.likelion.bebc25.recipe.main.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MainDto {

    // 게시글 번호
    private int id;

    // 멤버 아이디
    private int memberId;

    // 대표 이미지
    private String mainImage;

    // 카테고리 ID: 한식 양식 중식 일식
    private int categoryId;

    // 게시글 제목
    private String title;

    // 조회수
    private int viewCount;

    // 작성일자
    private LocalDateTime createdAt;

    // 게시판 타입 (1: 레시피 / 2: 요리꿀팁)
    private int postType;

    // 카테고리 이름
    private String categoryName;

    // 작성자 이름
    private String memberName;

    // 좋아요 수 (LIKE 테이블 COUNT)
    private int likeCount;

    // 팔로워 수 (밥플루언서용)
    private int followerCount;

}