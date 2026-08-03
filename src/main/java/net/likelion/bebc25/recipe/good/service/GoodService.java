package net.likelion.bebc25.recipe.good.service;

import net.likelion.bebc25.recipe.good.dto.GoodDto;
import net.likelion.bebc25.recipe.post.dto.PostDto;

import java.util.List;

public interface GoodService {
    // 좋아요를 누르거나 취소
    void toggleLike(GoodDto goodDto);

    // 특정 유저가 이 글에 좋아요를 눌렀는지 여부 조회
    boolean isLiked(GoodDto goodDto);

    /**
     * 입력 받은 회원 ID로 해당 회원이 스크랩한 레시피 조회
     * @param memberId 조회할 회원 ID
     * @return 조회된 레시피 리스트
     */
    List<PostDto> getRecipes(int memberId);

}
