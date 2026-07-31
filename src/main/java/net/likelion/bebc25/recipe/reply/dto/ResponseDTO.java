package net.likelion.bebc25.recipe.reply.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ResponseDTO {

    // 댓글 고유 번호
    private int id;
    // 게시글 번호/
    private int postId;
    // 작성자 고유 번호
    private int memberId;


    //화면에 직접 보여줄 데이터들
    // 작성자 닉네임/이름 (member 테이블과 조인해서 가져옴)
    private String name;

    // 댓글 내용
    private String content;
    // 작성 일시
    private LocalDateTime createdAt;
}
