package net.likelion.bebc25.recipe.reply.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RequestDTO {


    //특정 게시글에 달린 댓글
    private Long postId;

    //작성자가 입력한 댓글 내용
    private String content;

    //작성자
    private Long memberId;
}
