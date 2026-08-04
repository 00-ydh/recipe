package net.likelion.bebc25.recipe.good.service;


import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.good.respository.GoodRepository;
import net.likelion.bebc25.recipe.post.dto.PostDto;
import net.likelion.bebc25.recipe.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodServiceImpl implements GoodService {
    private final GoodRepository goodRepository;
    private final PostRepository postRepository;

    public GoodServiceImpl(GoodRepository goodRepository, PostRepository postRepository) {
        this.goodRepository = goodRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void toggleLike(GoodDto goodDto) {
        // 이미 눌렀는지 확인
        boolean alreadyLiked = goodRepository.exists(goodDto);

        if (alreadyLiked) {
            // 이미 눌렀다면 취소 (DELETE)
            goodRepository.delete(goodDto);

            // ★ likeType이 1(레시피 좋아요) 또는 2(팁 좋아요)일 때 감소
            if (goodDto.getLikeType() == 1 || goodDto.getLikeType() == 2) {
                postRepository.decreaseLikeCount(goodDto.getPostId());
            }
        } else {
            // 안 눌렀다면 추가 (INSERT)
            goodRepository.save(goodDto);

            // ★ likeType이 1(레시피 좋아요) 또는 2(팁 좋아요)일 때 증가
            if (goodDto.getLikeType() == 1 || goodDto.getLikeType() == 2) {
                postRepository.increaseLikeCount(goodDto.getPostId());
            }
        }
    }

    @Override
    public boolean isLiked(GoodDto goodDto) {
        return goodRepository.exists(goodDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostDto> getRecipes(int memberId) {
        return postRepository.getScrapRecipes(memberId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostDto> getTips(int memberId) {
        return postRepository.getScrapTips(memberId);
    }
}
