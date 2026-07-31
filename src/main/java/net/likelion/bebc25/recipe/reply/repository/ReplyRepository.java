package net.likelion.bebc25.recipe.reply.repository;

import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;

import java.util.List;

public interface ReplyRepository {
    //댓글 저장
    void save(RequestDTO requestDTO);
    //특정 게시글에 달린 댓글 목록 조회
    List<ResponseDTO> findByPostId(int postId);
    //댓글 삭재
    void deleteById(int id);

    ResponseDTO findById(int id);

}
