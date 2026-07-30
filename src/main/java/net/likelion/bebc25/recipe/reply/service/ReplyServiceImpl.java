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
    public List<ResponseDTO> getRepliesByPostId(Long postId) {
        return replyRepository.findByPostId(postId);
    }

    @Override
    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }
}