package net.likelion.bebc25.recipe.reply.service;

import net.likelion.bebc25.recipe.reply.dto.ResponseDTO;
import net.likelion.bebc25.recipe.reply.dto.RequestDTO;
import net.likelion.bebc25.recipe.reply.repository.ReplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    public ReplyServiceImpl(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @Override
    public void writeReply(RequestDTO requestDTO) {
        replyRepository.save(requestDTO);
    }

    @Override
    public List<ResponseDTO> getRepliesByPostId(int postId) {
        return replyRepository.findByPostId(postId);
    }

    @Override
    public void deleteReply(int id) {
        replyRepository.deleteById(id);
    }

    @Override
    public ResponseDTO findById(int id) {
        return replyRepository.findById(id); // 리포지토리(또는 DAO)를 호출해서 결과를 반환
    }
}