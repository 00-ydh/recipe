package net.likelion.bebc25.recipe.good.service;


import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.good.respository.GoodRepository;
import org.springframework.stereotype.Service;

@Service
public class GoodServiceImpl implements GoodService {
    private final GoodRepository goodRepository;

    public GoodServiceImpl(GoodRepository goodRepository) {
        this.goodRepository = goodRepository;
    }

    @Override
    public void toggleLike(GoodDto goodDto) {
        // 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = goodRepository.exists(goodDto);

        if (alreadyLiked) {
            //  이미 눌렀다면 취소 (DELETE)
            goodRepository.delete(goodDto);
        } else {
            //  안 눌렀다면 추가 (INSERT)
            goodRepository.save(goodDto);
        }
    }


    @Override
    public boolean isLiked(GoodDto goodDto) {
        return goodRepository.exists(goodDto);
    }


}
