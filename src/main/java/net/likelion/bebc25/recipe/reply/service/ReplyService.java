package net.likelion.bebc25.recipe.reply.service;

import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.dto.RequestDTO;

import java.util.List;

public interface ReplyService {
    //댓글 작성
    void writeReply(RequestDTO requestDTO);

    //특정 게시글의 댓글 목록 조회
    List<ResponseDTO> getRepliesByPostId(int postId);

    //댓글 삭제
    void deleteReply(int id);

    ResponseDTO findById(int id);
}