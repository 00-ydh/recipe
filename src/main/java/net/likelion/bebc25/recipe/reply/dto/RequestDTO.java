package net.likelion.bebc25.recipe.reply.dto;



import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {


    //특정 게시글에 달린 댓글
    private int postId;

    //작성자가 입력한 댓글 내용
    private String content;

    //작성자
    private int memberId;
}
