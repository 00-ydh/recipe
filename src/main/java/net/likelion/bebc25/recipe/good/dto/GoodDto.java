package net.likelion.bebc25.recipe.good.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodDto {

    // 게시글 번호
    private int postId;

    // 좋아요 타입 (게시판 종류 구분용, 예: 레시피=1, 팁=2 스크랩 = 3)
    private int likeType;

    // 리다이렉트나 분기 처리를 위한 게시판 타입 (예: "recipe", "tip")
    private String postType;

    private int memberId;
}