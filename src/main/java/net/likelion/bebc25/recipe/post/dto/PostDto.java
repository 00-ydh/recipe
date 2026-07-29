package net.likelion.bebc25.recipe.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {
    // 게시글 번호
    private int id;

    // 멤버 아이디
    private int memberId;

    // 대표 이미지
    private String main_image;

    // 제목
    private String title;

    // 내용
    private String content;


}
