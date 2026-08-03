package net.likelion.bebc25.recipe.good.service;

import net.likelion.bebc25.recipe.good.dto.GoodDto;

public interface GoodService {
    // 좋아요를 누르거나 취소
    void toggleLike(GoodDto goodDto);

    // 특정 유저가 이 글에 좋아요를 눌렀는지 여부 조회
    boolean isLiked(GoodDto goodDto);


}
